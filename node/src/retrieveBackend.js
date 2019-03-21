const request = require('request');
let io = require('./socket.io')();
const StateObj = require('./stateobj');

const pollSatInterval = 3000;
const pollPlaneInterval = 10000;
const pollMsgInterval = 1000;

if (!io) {
    setTimeout(() => {
        io = require('./socket.io')();
        io.setConnect(connect);
    }, 500); // Wait for initialization
} else {
    io.setConnect(connect);
}

let url = "http://51.38.113.39:8090";
let planesUrl = "https://opensky-network.org/api/states/all";

const planeNum = 20;

let messages = [];
const usersState = new StateObj();
const markerState = new StateObj();
const beamState = new StateObj();
const satState = new StateObj();
const planesState = new StateObj();


function connect(client) {
    // This function is called every time when a new client connects.
    // We shall immediately flush the current known data
    setTimeout(() => {
        io.broadcast('users', Object.values(usersState.current()).map( n => Object.assign({}, n, { state: 'new'})));
        io.broadcast('satellites', Object.values(satState.current()).map(n => Object.assign({}, n, { state: 'new'})));
        io.broadcast('markers', Object.values(markerState.current()).map(n => Object.assign({}, n, { state: 'new'})));
        io.broadcast('planes', Object.values(
            planesState.current())
            .map(n => Object.assign({}, n, { state: 'new'}))
            .sort((a,b) => a.id < b.id ? -1:1)
            .slice(0,planeNum)
            .filter(n => n.data.latlng[0] != null && n.data.latlng[0] != 0 && n.data.latlng[1] != null && n.data.latlng[1] != 0));
        io.broadcast('messages', messages);
    }, 300);

    client.socket.on('moveonemarker', data => {
        io.broadcast('moveonemarker', data);
    });
}

function broadcastMsg() {
    //console.log('messages processing');
    messages = messages.splice(-100); // Leave only the last 100 messages
    request(url+'/systemlogs', (err, resp, body) => {
       // console.log('systemlogs', err, body);
        if (err || (!body)) return;
        let m = [];
        for (let line of body.split('</br>')) {
            if (messages.indexOf(line)<0) {
                messages.push(line);
                m.push(line);
            }
        }
        if (m.length>0) io.broadcast('messages', m);
    });
    request(url+'/eventlogs', (err, resp, body) => {
        if (err || (!body)) return;
        let m = [];
        for (let line of body.split('</br>')) {
            if (messages.indexOf(line)<0) {
                messages.push(line);
                m.push(line);
            }
        }
        if (m.length>0) io.broadcast('messages', m);
    });
    request(url+'/sadremalogs', (err, resp, body) => {
        if (err || (!body)) return;
        let m = [];
        for (let line of body.split('</br>')) {
            if (messages.indexOf(line)<0) {
                messages.push(line);
                m.push(line);
            }
        }
        if (m.length>0) io.broadcast('messages', m);
    });
}

function broadcastSat() {
    request(url + '/world', (err, resp, body) => {
        console.log('World processing');
        if (err) {
            console.log('Request, err', err);
            return;
        }
        let jsonObj = JSON.parse(body);
        if (io) {
            let users = Object.values(jsonObj.mapUser);
            let step = users
                .map(
                    n => Object.values(n.mapMarker).map(
                        m => {
                            m.slot = n.slot;
                            m.user = Object.assign({}, n);
                            delete m.user.mapMarker; // Drop the cyclic loop
                            return m;
                        }));

            if (typeof step != 'object' || step.length < 1) {
                return; // Broken data
            }

            users.forEach(n => usersState.update(n.uuid, n));
            let markers = step.reduce((x, y) => x.concat(y));
            markers.forEach(n => markerState.update(n.uuid, n));
            Object.values(jsonObj.mapSatellite).forEach(n => satState.update(n.uuid, n));

            // Lets create the beams
            Object.values(jsonObj.mapSatellite).forEach(s => {
                Object.values(s.mapBeam).forEach(n => {
                    n.satRef = Object.assign({}, s);
                    delete n.satRef.mapBeam; // Drop the cyclic reference
                    beamState.update(n.uuid, n);
                });
            });

            let o = Object.values(usersState.retrieve());

            io.broadcast('users', o);
            io.broadcast('markers', Object.values(markerState.retrieve()));
            io.broadcast('satellites', Object.values(satState.retrieve()));
            io.broadcast('beams', Object.values(beamState.retrieve()));
        }
    
    });
}


function broadcastPlanes() {
    request(planesUrl, (err, resp, body) => {
        let data = JSON.parse(body);
        console.log('Some planes data');

        data.states.forEach(n => {
            let old = planesState.get(n[0]);
            let obj = {
                id: n[0],
                n: n,
                latlng: [n[6], n[5]],
                deg: n[10] || 0,
                speed: n[9],
                change: true
            };

            if (old !== null && old.data &&
                old.data.latlng[0] == obj.latlng[0] &&
                old.data.latlng[1] == obj.latlng[1]) {
                obj.change = false;
            }

            if (obj.latlng[0] == null || obj.latlng[1] == null) {
                obj.change = false;
            }

            planesState.update(n[0], obj);
        });

        io.broadcast('planes',
            Object.values(planesState.retrieve())
                .sort((a,b) => a.id < b.id ? -1:1)
                .slice(0,planeNum)
                .filter(n => n.data.change)
                .filter(n => n.data.latlng[0] != null && n.data.latlng[0] != 0 && n.data.latlng[1] != null && n.data.latlng[1] != 0));
    });
}

setInterval(() => broadcastSat(), pollSatInterval);
setInterval(() => broadcastPlanes(), pollPlaneInterval);
setInterval(() => broadcastMsg(), pollMsgInterval);
