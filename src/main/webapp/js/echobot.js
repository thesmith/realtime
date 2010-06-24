var BOSH_SERVICE = 'http://bosh.metajack.im:5280/xmpp-httpbind';
var connection = null;

function log(msg) {
	try {
    $('#log').html(document.createTextNode(msg));
	} catch (e) {
		$('#log').html('error outputting: '+msg);
	}
}

function add_video(video) {
	$('#video').animate({'left': '80em'}, 1000, null, function() {
		$(this).css('display', 'none');
		$(this).animate({'left': '-80em'}, 0, null, function() {
			$('#video .desc').html(video.desc);
			$('#video .title').children('.link').html(video.title);
			$('#video .link').attr('href', video.link);
			$('#video .image').attr('src', video.image);
			$(this).css('display', 'block');
			
	  	$(this).animate({'left': '0px'}, 1000);
	  });
	});
}

function onConnect(status)
{
    if (status == Strophe.Status.CONNECTING) {
	    log('Strophe is connecting.');
    } else if (status == Strophe.Status.CONNFAIL) {
	    log('Strophe failed to connect.');
	    $('#connect').get(0).value = 'connect';
    } else if (status == Strophe.Status.DISCONNECTING) {
	    log('Strophe is disconnecting.');
    } else if (status == Strophe.Status.DISCONNECTED) {
	    log('Strophe is disconnected.');
	    $('#connect').get(0).value = 'connect';
    } else if (status == Strophe.Status.CONNECTED) {
	    log('Strophe is connected.');
	    log('ECHOBOT: Send a message to ' + connection.jid + ' to talk to me.');

	    connection.addHandler(onMessage, null, 'message', null, null,  null); 
	    connection.send($pres().tree());
    }
}

function onMessage(msg) {
    var to = msg.getAttribute('to');
    var from = msg.getAttribute('from');
    var type = msg.getAttribute('type');
    var elems = msg.getElementsByTagName('body');

    if (type == "chat" && elems.length > 0) {
	    var body = Strophe.getText(elems[0]);
	    log('ECHOBOT: I got a message from ' + from + ': ' + body);
	
			try {
	      var video = eval('('+body+')');
	      add_video(video);
	    } catch(e) {
				log('Unable to parse: '+'('+body+')');
	    }
    
//	var reply = $msg({to: from, from: to, type: 'chat'})
//            .cnode(Strophe.copyElement(body));
//	connection.send(reply.tree());

//	log('ECHOBOT: I sent ' + from + ': ' + Strophe.getText(body));
    }

    // we must return true to keep the handler alive.  
    // returning false would remove it after it finishes.
    return true;
}

$(document).ready(function () {
    connection = new Strophe.Connection(BOSH_SERVICE);

    // Uncomment the following lines to spy on the wire traffic.
    //connection.rawInput = function (data) { log('RECV: ' + data); };
    //connection.rawOutput = function (data) { log('SEND: ' + data); };

    // Uncomment the following line to see all the debug output.
    //Strophe.log = function (level, msg) { log('LOG: ' + msg); };


    $('#connect').bind('click', function () {
	    var button = $('#connect').get(0);
	    if (button.value == 'connect') {
	      button.value = 'disconnect';
	      connection.connect($('#jid').get(0).value, $('#pass').get(0).value, onConnect);
	    } else {
	      button.value = 'connect';
	      connection.disconnect();
	    }
    });
});

function startUpdating() {
	$.ajax({
		url: 'http://realtime-video.appspot.com/latest/send',
		cache: false,
		success: function(data) {
			log("updated");
		}, 
		error: function(error) {
		  log("error");
		}
	});
}