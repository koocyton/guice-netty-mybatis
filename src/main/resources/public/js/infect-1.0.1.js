(function ($) {

    'use strict';

    $.extend({
        logger: function () {
            if (window.console && window.console.log && arguments.length >= 1) {
                window.console.log("arguments.length : " + arguments.length);
                for (var ii = 0; ii < arguments.length; ii++) {
                    window.console.log(arguments[ii]);
                }
            }
        }
    });

    $.extend({

        websocket: function(uri) {

             let connect = function(uri) {

                let protocol = /^https/.test(window.location.protocol) ? "wss\:\/\/" : "ws\:\/\/";

                let ws = /^ws/.test(uri) ? new WebSocket(uri) : new WebSocket(protocol + window.location.host + uri);

                ws.onopen = function(){
                    console.log("socket has been opened");
                };

                ws.onmessage = function(e) {
                    $.logger(e.data)
                };

                return ws;
            };

            let ws = connect(uri);
            ws.onclose = function() {
                setTimeout(function(){
                    ws = connect(uri);
                }, 5000);
            };

            this.send = function(message) {
                ws.send(message);
            };

            return this;
        }
    });

    $.extend({
        infect : function (wsUrl) {

            let ws = $.websocket(wsUrl);
            let pixiApp = null;
            let pixiBox = null;

            this.init = function() {
                pixiApp = new PIXI.Application($(window).width(), $(window).height(), {transparent: true});
                pixiBox = $(".game-box");
                pixiBox.prepend(pixiApp.view);
                return this;
            };

            this.send = function(message) {
                ws.send(message);
            };

            this.resize = function() {
                pixiBox.children("canvas").css({width: $(window).width(), height: $(window).height()});
            };

            return this.init();
        }
    });

    $(document).ready(function () {
        let ift = $.infect("/game-socket");
        $(window).bind("resize", function () {
            ift.resize();
        }).trigger("resize");
    });

})(jQuery);