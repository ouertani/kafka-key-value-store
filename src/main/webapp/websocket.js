var ws;

function connect() {
    var key = document.getElementById("key").value;

    var host = document.location.host;
    var pathname = document.location.pathname;

    console.log( key);
    var host2 = "localhost:8081" ;
    var pathname2= "/"
    //ws = new WebSocket("ws://" +host2  + pathname2 + "wss/" + key);

    ws = new WebSocket("ws://localhost:8080/events/");
    ws.onmessage = function(event) {
    //var log = document.getElementById("log");
        console.log(event.data+"TEST");
      //  var message = JSON.parse(event.data);
       // log.innerHTML += message.from + " : " + message.content + "\n";
        log.innerHTML +=  event.data + "\n";
    };

    // When the connection is open, send some data to the server
    ws.onopen = function () {
      ws.send(key); // Send the message 'Ping' to the server
    };

    // Log errors
    ws.onerror = function (error) {
      console.log('WebSocket Error ' + error);
    };
}