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

io.on('connection', function(socket){
	//console.log('a user connected');
	//io.emit('chat message', 'new user connected: ' + socket.id);
	socket.on('chat', function(msg){
		//console.log('message: ' + msg);
		io.emit('chat', {'sender':'aa', 'content':msg});
		//socket.broadcast.emit('chat message', msg);
	});
	socket.on('disconnect', function(){
		//console.log('a user disconnected');
	});
});