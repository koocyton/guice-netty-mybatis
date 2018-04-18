(function ($) {

    'use strict';

    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes(),
            "s+": this.getSeconds(),
            "q+": Math.floor((this.getMonth() + 3) / 3),
            "S": this.getMilliseconds()
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    $.extend({
        logger: function () {
            if (window.console && window.console.log && arguments.length >= 1) {
                let dateTime = new Date().Format("yyyy-MM-dd hh:mm:ss");
                window.console.log("[" + dateTime + "] arguments.length : " + arguments.length);
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
                    $.logger("socket has been opened");
                };

                ws.onmessage = function(e) {
                    $.logger(e.data)
                };

                return ws;
            };

            let ws = connect(uri);
            ws.onclose = function() {
                $.logger("socket has been closed");
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
            };

            this.send = function(message) {
                ws.send(message);
            };

            this.resize = function() {
                pixiBox.children("canvas").css({width: $(window).width(), height: $(window).height()});
            };

            this.init();
            return this;
        }
    });

    $(document).ready(function () {
        window.infect = $.infect("/game-socket");
        $(window).bind("resize", function(){
            window.infect.resize();
        }).trigger("resize");
    });

})(jQuery);