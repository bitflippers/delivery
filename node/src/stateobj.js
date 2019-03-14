class StateObj {
    constructor () {
        this.version = 1;
        this.obj = {};
    }

    update(id, o) {
        if (this.obj[id]) {
//            console.log('update', id, o);
            this.obj[id].data = o;
            this.obj[id].state = 'update';
            this.obj[id].version = this.version;
        } else {
//            console.log('new', id, o);
            this.obj[id] = {
                id: id,
                data: o,
                state: 'new',
                version: this.version
            }
        }
    }

    get(id) {
        if (this.obj[id]) return this.obj[id];
        return null;
    }

    retrieve() {
        let obj = Object.assign({}, this.obj);
        for (let o of Object.values(obj)) {
            if (o.version !== this.version) {
                o.state = 'delete';
                delete this.obj[o.id];
            }
        }
        this.version++; // Change the state
        return obj;
    }

    current() {
        return Object.assign({}, this.obj);
    }
}

module.exports = StateObj;
