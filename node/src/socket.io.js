let io = undefined;

class IOclient {
    constructor(socket) {
        this.socket = socket;

        this.socket.on('error', err => {
            console.log('Error', err);
        })
    }

    disconnect(cb) {
        if (cb) {
            this.socket.on('disconnect', data => {
                cb(data);
            })
        }
    }

    reconnect(cb) {
        if (cb) {
            this.socket.on('reconnect', data => {
                cb(data);
            })
        }
    }

    error(cb) {
        if (cb) {
            this.socket.on('error', err => {
                cb(err);
            })
        }
    }
}

class IOmsg {
    constructor(server) {
        this.list = [];
        this.io = require('socket.io')(server);
        this.io.on('connection', socket => {
            let client = new IOclient(socket);
            this.list.push(client);
            client.disconnect(() => {
                console.log('Disconnecting client');
                if (this.list.indexOf(client)>=0) {
                    this.list.splice(this.list.indexOf(client), 1);
                }
            });
            if (this.connectCb) {
                this.connectCb(client);
            }
        });
    }

    broadcast(msg, data) {
        this.io.emit(msg, data);
    }

    setConnect(cb) {
        this.connectCb = cb;
    }
}

module.exports = (server) => {
    if (server) io = new IOmsg(server);
    return io;
}
