const request = require('request');
var io = require('./socket.io')();

const pollInterval = 3000;

if (!io) {
    setTimeout(() => io = require('./socket.io')(), 500); // Wait for initialization
}

let url = "http://127.0.0.1:8090/world";

let planesUrl = "https://opensky-network.org/api/states/all";

let userState = {};
let oldMarkers = {};
let oldPlanes = {};

function broadcast() {
    request(url, (err, resp, body) => {
        if (err) {
            console.log('Request, err', err);
            return;
        }
        let jsonObj = JSON.parse(body);
        if (io) {
            let users = Object.values(jsonObj.mapUser);
            let markers = Object.values(jsonObj.mapUser)
                .map(n => Object.values(n.mapMarker).map(m => { m.slot = n.slot; return m }))
                .reduce((x, y) => x.concat(y));
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


            io.broadcast('users', Object.values(jsonObj.mapUser));
            io.broadcast('markers', Object.values(objm));
            
            oldMarkers = {};
            Object.values(objm).filter(n => n.state != 'delete').forEach(n => oldMarkers[n.uuid] = n);

            // Fake messages
            let messages = [];
            for (i = 0; i<Math.random() * 3; i++) {
                messages.push('Test message '+parseInt(Math.random()*100));
            }
            if (messages.length>0) 
                io.broadcast('messages', messages);

            console.log('xx',jsonObj.mapSatellite);
            io.broadcast('satellites', Object.values(jsonObj.mapSatellite));
        }
    
    });

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
    })
}


setInterval(() => broadcast(), pollInterval);
