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
            let protocol = /^https/.test(window.location.protocol) ? "wss\:\/\/" : "ws\:\/\/";
            return /^ws/.test(uri) ? new WebSocket(uri) : new WebSocket(protocol + window.location.host + uri);
        }
    });

    $.extend({
        infect : function (wsUrl) {
            this.ws = $.websocket(wsUrl);
            this.send = function(message){
                $.logger.info(this.ws);
                this.ws.send(message);
            };
        }
    });

    // let Infect = function() {
    //     this.pixiApp = null;
    //     this.pixiBox = null;
    //     this.gameSkt = null;
    // };
    //
    // Infect.prototype.init = function(width, height) {
    //     this.pixiApp = new PIXI.Application(width, height, {transparent: true});
    //     this.pixiBox = $(".game-box");
    //     this.pixiBox.prepend(this.pixiApp.view);
    //     return this;
    // };
    //
    // Infect.prototype.connect = function(wsUrl) {
    //     this.gameSkt = $.websocket(wsUrl);
    //     return this;
    // };
    //
    // Infect.prototype.resize = function(width, height) {
    //     this.pixiBox.children("canvas").css({width: width, height: height});
    // };

    $(document).ready(function () {
        let a = $.infect("/game-socket");
        $(window).bind("resize", function () {
            a.send("/game-socket");
            // infect.resize($(window).width(), $(window).height());
        }).trigger("resize");
    });

})(jQuery);