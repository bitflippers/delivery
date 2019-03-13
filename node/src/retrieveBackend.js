const request = require('request');
var io = require('./socket.io')();

const pollSatInterval = 3000;
const pollPlaneInterval = 10000;
const pollMsgInterval = 1500;

if (!io) {
    setTimeout(() => {
        io = require('./socket.io')();
        io.setConnect(connect);
    }, 500); // Wait for initialization
} else {
    io.setConnect(connect);
}

let url = "http://127.0.0.1:8090";

let planesUrl = "https://opensky-network.org/api/states/all";

let userState = {};
let oldMarkers = {};
let oldPlanes = {};
let oldUsers = [];
let oldSatellites = [];
let messages = [];

function connect(client) {
    // This function is called every time when a new client connects.
    // We shall immediatelly flush the current known data
    setTimeout(() => {
        io.broadcast('users', oldUsers);
        io.broadcast('satellites', oldSatellites);
        io.broadcast('markers', Object.values(oldMarkers).map(n => { n.state = 'new'; return n; }));
        io.broadcast('planes', Object.values(oldPlanes));
        io.broadcast('messages', messages);
    }, 300);

    client.socket.on('moveonemarker', data => {
        console.log('Move one marker', data);
        io.broadcast('moveonemarker', data);
    });
}

function broadcastMsg() {
    messages = messages.splice(-100); // Leave only the last 100 messages
    request(url+'/systemlogs', (err, resp, body) => {
        if (err || (!body)) return;
        let m = [];
        for (line in body.split('</br>')) {
            if (messages.indexOf(line)<0) {
                messages.push(line);
                m.push(line);
            }
        }
        io.broadcast('messages', m);
    });
    request(url+'/eventlogs', (err, resp, body) => {
        if (err || (!body)) return;
        let m = [];
        for (line in body.split('</br>')) {
            if (messages.indexOf(line)<0) {
                messages.push(line);
                m.push(line);
            }
        }
        io.broadcast('messages', m);
    });
    request(url+'/sadremalogs', (err, resp, body) => {
        if (err || (!body)) return;
        let m = [];
        for (line in body.split('</br>')) {
            if (messages.indexOf(line)<0) {
                messages.push(line);
                m.push(line);
            }
        }
        io.broadcast('messages', m);
    });
}

function broadcastSat() {
    request(url + '/world', (err, resp, body) => {
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

            let markers = step.reduce((x, y) => x.concat(y));
            let objm = {};
            markers.forEach(n => objm[n.uuid] = n);
            let newKeys = Object.keys(objm);
            let oldKeys = Object.keys(oldMarkers);

            newKeys.filter(n => oldKeys.indexOf(n)>=0).forEach(n => {
                objm[n].state = 'update';
                if (newKeys.indexOf(n)>=0) newKeys.splice(newKeys.indexOf(n),1);
                if (oldKeys.indexOf(n)>=0) oldKeys.splice(oldKeys.indexOf(n),1);
            });

            newKeys.forEach(n => {
                objm[n].state = 'new';
            });
            console.log('Old keys',oldKeys);
            oldKeys.forEach(n => {
//                console.log('Do delete');
                objm[n] = oldMarkers[n];
                objm[n].state = 'delete';
            });


            //console.log('xx',jsonObj.mapSatellite);
            oldSatellites = Object.values(jsonObj.mapSatellite);
            io.broadcast('satellites', oldSatellites);
            io.broadcast('users', users);
            io.broadcast('markers', Object.values(objm));

            oldUsers = users;
            
            oldMarkers = {};
            Object.values(objm).filter(n => n.state != 'delete').forEach(n => oldMarkers[n.uuid] = n);

        }
    
    });
}


function broadcastPlanes() {
    request(planesUrl, (err, resp, body) => {
        let data = JSON.parse(body);
        console.log('Some planes data');

        let d = data.states.sort((a,b) => a[0]<b[0] ? -1:1).slice(0,50).map(n => {
            let obj = {
                id: n[0],
                n: n,
                latlng: [n[6], n[5]],
                deg: n[10] || 0,
                speed: n[9],
                change: true
            };
            if (oldPlanes[obj.id] && (oldPlanes[obj.id].latlng[0] != obj.latlng[0]|| oldPlanes[obj.id].latlng[1] != obj.latlng[1])) {
                obj.change = true;
            } else {
                obj.change = false;
            }
            if (n[6] == null || n[5] == null || n[6] === 0 || n[5] === 0) {
                obj.change = false;
            }
            oldPlanes[obj.id] = obj;
            return obj;
        }).filter(n => n.change);

        io.broadcast('planes', d);
    });
}

setInterval(() => broadcastSat(), pollSatInterval);
setInterval(() => broadcastPlanes(), pollPlaneInterval);
setInterval(() => broadcastMsg(), pollMsgInterval);