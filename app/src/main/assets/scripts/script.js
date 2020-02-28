// **** NATIVE CONSOLE LOG **** //

function tryCallNative(data) {
    try {
        window.external.notify(data);
    } catch (err) {

    }
}

var console = (function(oldCons) {
	return {
		log: function(text) {
			oldCons.log(text);
			tryCallNative("Log: " + text);
		},
		info: function(text) {
			oldCons.info(text);
			tryCallNative("Info: " + text);
		},
		warn: function(text) {
			oldCons.warn(text);
			tryCallNative("Warning: " + text);
		},
		error: function(text) {
			oldCons.error(text);
			tryCallNative("Error: " + text);
		}
	};
 } (window.console));
//Then redefine the old console
window.console = console;


window.onerror = function(error) {
    console.log("window.onerror: " + error);
}


// **** WEBRTC UI **** //

var conferenceVideo;
var selfVideo
var bandwidth;
var conference;
var pin;
var isFrontFacing=false;


var rtc = null;

/* ~~~ SETUP AND TEARDOWN ~~~ */

function finalise(event) {
    rtc.disconnect();
    conferenceVideo.src = "";
}

function remoteDisconnect(reason) {
    console.log("remoteDisconnect " + reason);
    window.removeEventListener('beforeunload', finalise);
    window.close();
    app.closeActivity();
}

function disconnectFromCurrentMeeting() {
     console.log("Disconnected from current meeting");
     rtc.user_media_stream = null;
     rtc.disconnect();
 }

 function muteVideo(setting) {
     console.log("Mute video - "+setting);
     rtc.muteVideo(setting);
 }

 function muteAudio(setting) {
        console.log("Mute audio - "+setting);
        rtc.muteAudio(setting);
 }

 function toggleSelfview() {
        console.log("Toggle camera");
        setCameraStream(isFrontFacing);
 }

 function muteVolume(setting)
 {
        console.log("Mute Volume - "+setting);
        conferenceVideo.muted = setting;
 }

 function setCameraStream(type)
 {
     isFrontFacing = type;
     navigator.mediaDevices.getUserMedia({ video: {facingMode: isFrontFacing ? "user" : "environment"}, audio: true})
     .then(function(mediaStream)
     {
             rtc.user_media_stream = mediaStream;
             isFrontFacing = !isFrontFacing;
             rtc.renegotiate();
     })
     .catch(function(error) {})

     if(isFrontFacing)
     selfVideo.style.webkitTransform = "scale(-1, 1)";
     else
     selfVideo.style.webkitTransform = "scale(1, 1)";

 }

function doneSetup(videoUrl, pin_status) {
    console.log("Done setup - PIN status: " + pin_status);
    console.log("Done setup - Video url: " + videoUrl);

    rtc.connect(pin);
    if (videoUrl) {
        if (typeof(MediaStream) !== "undefined" && videoUrl instanceof MediaStream) {
            selfVideo.srcObject = videoUrl;
        } else {
            selfVideo.src = videoUrl;
        }
    }
}

function connected(videoUrl) {
    console.log('Video connected', videoUrl);

    if (videoUrl) {
        if (typeof(MediaStream) !== "undefined" && videoUrl instanceof MediaStream) {
            conferenceVideo.srcObject = videoUrl;
        } else {
            conferenceVideo.src = videoUrl;
        }
    }

}

function onParticipantCreate(participant) {
    console.log('onParticipantCreate', participant);
}

function onParticipantUpdate(participant) {
    console.log('onParticipantUpdate', participant);
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function initialisePexRTC(node, conference, domain, userbw, name, userpin) {
    selfVideo = document.getElementById("self-video");
    conferenceVideo = document.getElementById("conference-video");

    pin = userpin;
    bandwidth = parseInt(userbw);

    rtc = new PexRTC();

    window.addEventListener('beforeunload', finalise);

    rtc.onSetup = doneSetup;
    rtc.onConnect = connected;
    rtc.onError = remoteDisconnect;
    rtc.onDisconnect = remoteDisconnect;
    rtc.onParticipantCreate = onParticipantCreate;
    rtc.onParticipantCreate = onParticipantUpdate;

    console.log("rtc.makeCall11("+node+"," + conference + ", " + name + ", " + bandwidth + ")");
    rtc.makeCall(node, conference, name, bandwidth);

}

function initialiseJSAPI(node, conference, domain, userbw, name, userpin) {
    console.log("Initializing pexrtc js api with node: " + node);
    var script = document.createElement('script');
    script.onload = function () {
        console.log("Initialized pexrtc js api with node: " + node);
        initialisePexRTC(node, conference, domain, userbw, name, userpin)
    };
    script.src = "https://"+ node +"/static/webrtc/js/pexrtc.js";

    document.head.appendChild(script);
}

function initialise(node, conference, domain, userbw, name, userpin) {
    try {
        console.log("Initialise - Node: " + node);
        console.log("Initialise - Conference: " + conference);
        console.log("Initialise - Domain: " + domain);
        console.log("Initialise - Bandwidth: " + userbw);
        console.log("Initialise - Name: " + name);
        console.log("Initialise - User Pin: " + userpin);

        initialiseJSAPI(node, conference, domain, userbw, name, userpin)
    }
    catch(error) {
        console.log("Error catched in initialise: " + error);
    }
}

window.onload = function() {
    try{
        var conferenceNode = getParameterByName('cnode');
        var conferenceName = getParameterByName('cname');
        var bandwidth = getParameterByName('bw');
        var name = getParameterByName('name');
        var pin = getParameterByName('pin');
        var domain = getParameterByName('domain');

        if(conferenceNode, conferenceName, domain, bandwidth, name) {
            initialise(conferenceNode, conferenceName, domain, bandwidth, name, pin);
        }
    }
    catch (error) {
        console.log("Error catched in window.onload: ", error);
    }

}

