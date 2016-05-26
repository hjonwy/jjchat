var http = require('http').createServer(handler);
var io = require('socket.io')(http);
var fs=require('fs');

var port = 18080;
http.listen(port);


function handler (req, res) {
  fs.readFile(__dirname + '/index.html',
  function (err, data) {
    if (err) {
      res.writeHead(500);
      return res.end('Error loading index.html');
    }

    res.writeHead(200);
    res.end(data);
  });
}

var clients = {};

io.on('connection', function(socket){
	socket.on('chat', onChat); //custom msg.
	socket.on('login', onLogin); //custom msg.
	socket.on('logout', onLogout); //custom msg.
	socket.on('disconnect', onDisconnect);//built in event.
});

function onChat(msg){
	var user=clients[this.id];
	if(user){
		io.emit('chat', {'sender':user.name,'content':msg});	
	}else{
		io.emit('chat', {'sender':'unkown','content':msg});
	}
}

function onLogin(msg){
	var user = clients[this.id];
	if(user){
		user.name=msg.name;
	}
	else{
		clients[this.id]={'name':msg.name};
	}
	
	io.emit('login', {'name':clients[this.id].name});
}

function onLogout(msg){
	
}

function onDisconnect(){
	var user=clients[this.id];
	if(user){
		io.emit('disconnect', {'name':user.name});
	}
	
	delete clients[this.id];
	
	/*
	var output="";
	for (var key in clients) {
            if (output == "") {
                output = clients[key].name;
            }
            else {
                output += "|" + clients[key].name;
            }
        }
		
        console.log(output);
		*/
}