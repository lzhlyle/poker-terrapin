var index = (function () {
    var self = {};
    var $name;
    var stompClient = null;

    self.init = function (fn) {
        connect();
        return fn();
    };

    function connect() {
        var socket = new SockJS('/terrapin');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/topic/log', function (response) {
                var data = JSON.parse(response.body);
                appendLog(data.name, data.message);
            });

            stompClient.subscribe('/topic/players', function (response) {
                var data = JSON.parse(response.body);
                refreshPlayers(data.players, data.banker);
            });
        });

        function appendLog(name, txt) {
            if (!txt || !name) return;

            var p = $('<p class="text-left"></p>');
            p.html('【' + name + '】' + txt);
            $('#dv-log').prepend(p);
        }
    }

    function refreshPlayers(players, banker) {
        if (!players) return;

        var $playerBtns = $('.terrapin-player-btn');
        var $bankerBtns = $('.terrapin-banker-btn');
        $playerBtns.show();
        $bankerBtns.hide();
        if (!!banker && banker.name === $name) {
            $playerBtns.hide();
            $bankerBtns.show();
        }

        var $dv = $('#dv-players');
        $dv.html('');
        for (var i = 0, len = players.length; i < len; i++) {
            var player = players[i];
            var playerTxt = player.score.value + '【' + player.name + '】';

            if (!!player.handCardStr) {
                if (player.name === $name) {
                    var handCardStr = '您的手牌：' + player.handCardStr;
                    $('#dv-my').html('<h3>' + handCardStr + '</h3>');
                    playerTxt += player.handCardStr;
                } else {
                    playerTxt += isDisplayHandCard(player.status) ? player.handCardStr : '{{?, ?}, {?, ?}}';
                }
            }

            if (!!banker && banker.name === player.name) {
                // 庄家行
                var $b = $('<h4>' + playerTxt + ' ' + banker.count + '连庄' + '</h4>');
                $dv.prepend($b);
            } else {
                // 闲家行
                var $playerDv = $('<div></div>');
                playerTxt += ' ' + getStatus(player.status);
                $playerDv.append($('<span>' + playerTxt + '</span> '));

                if (!!banker && banker.name === $name) {
                    // 庄家看
                    if (player.status === 'PLAYER_PASS') {
                        $playerDv.append(generateBankerBtn('btn-banker-pass', '走过', player.name));
                        $playerDv.append(generateBankerBtn('btn-banker-kill', '杀牌', player.name));
                    } else if (player.status === 'PLAYER_COVER') {
                        $playerDv.append(generateBankerBtn('btn-banker-turnOver', '开牌', player.name));
                        $playerDv.append(generateBankerBtn('btn-banker-notTurnOver', '不开', player.name));
                    }
                }

                if (player.name === $name) {
                    // 闲家看
                    if (player.status === 'PLAYER_PASS_BANKER_PASS') {
                        $playerDv.append(generateBankerBtn('btn-player-pass-believe', '信咯', player.name));
                        $playerDv.append(generateBankerBtn('btn-player-pass-not-believe', '信戳', player.name));
                    } else if (player.status === 'PLAYER_PASS_BANKER_KILL') {
                        $playerDv.append(generateBankerBtn('btn-player-kill-believe', '信咯', player.name));
                        $playerDv.append(generateBankerBtn('btn-player-kill-not-believe', '信戳', player.name));
                    }
                }

                $dv.append($playerDv);
            }
        }// for

        bindDynamicBankerBtnEvent();

        function bindDynamicBankerBtnEvent() {
            // 庄家
            $('#btn-banker-pass').click(function () {
                index.bankerPass($(this).attr('data-player'));
            });
            $('#btn-banker-kill').click(function () {
                index.bankerKill($(this).attr('data-player'));
            });
            $('#btn-banker-turnOver').click(function () {
                index.bankerTurnOver($(this).attr('data-player'));
            });
            $('#btn-banker-notTurnOver').click(function () {
                index.bankerNotTurnOver($(this).attr('data-player'));
            });

            // 闲家
            $('#btn-player-pass-believe').click(function () {
                index.bankerPassBelieve();
            });
            $('#btn-player-pass-not-believe').click(function () {
                index.bankerPassNotBelieve();
            });
            $('#btn-player-kill-believe').click(function () {
                index.bankerKillBelieve();
            });
            $('#btn-player-kill-not-believe').click(function () {
                index.bankerKillNotBelieve();
            });
        }

        function generateBankerBtn(id, txt, player) {
            return $('<button type="button" class="btn btn-default terrapin-banker-btn" ' +
                'id="' + id + '" data-player="' + player + '">' + txt + '</button>');
        }

        function isDisplayHandCard(status) {
            switch (status) {
                case 'PLAYER_PASS' :
                case 'PLAYER_PASS_BANKER_PASS' :
                case 'PLAYER_PASS_BANKER_PASS_PLAYER_BELIEVE':
                case 'PLAYER_PASS_BANKER_PASS_PLAYER_NOT_BELIEVE':
                case 'PLAYER_PASS_BANKER_KILL' :
                case 'PLAYER_PASS_BANKER_KILL_PLAYER_BELIEVE':
                case 'PLAYER_PASS_BANKER_KILL_PLAYER_NOT_BELIEVE':
                case 'PLAYER_COVER_BANKER_TURN_OVER' :
                case 'PLAYER_FORCE' :
                case 'OPEN' :
                    return true;
                default:
                    return false;
            }
        }

        function getStatus(status) {
            switch (status) {
                case 'WAITING' :
                    return '摆牌中..';
                case 'PLAYER_PASS' :
                    return '低调求走';
                case 'PLAYER_PASS_BANKER_PASS' :
                    return '庄家走过';
                case 'PLAYER_PASS_BANKER_KILL' :
                    return '庄家杀牌';
                case 'PLAYER_COVER' :
                    return '盖牌硬刚';
                case 'PLAYER_COVER_BANKER_TURN_OVER' :
                    return '庄家开牌';
                case 'PLAYER_COVER_BANKER_NOT_TURN_OVER' :
                    return '庄家不开';
                case 'PLAYER_FORCE' :
                    return '高调强攻';
                case 'PLAYER_PASS_BANKER_PASS_PLAYER_BELIEVE':
                    return '闲家信走';
                case 'PLAYER_PASS_BANKER_PASS_PLAYER_NOT_BELIEVE':
                    return '闲家不信走';
                case 'PLAYER_PASS_BANKER_KILL_PLAYER_BELIEVE':
                    return '闲家信杀';
                case 'PLAYER_PASS_BANKER_KILL_PLAYER_NOT_BELIEVE':
                    return '闲家不信杀';
                default:
                    return '哪来的猴子';
            }
        }
    }

    self.bankerPassBelieve = function () {
        send('/banker/pass/believe', {});
    };

    self.bankerPassNotBelieve = function () {
        send('/banker/pass/not-believe', {});
    };

    self.bankerKillBelieve = function () {
        send('/banker/kill/believe', {});
    };

    self.bankerKillNotBelieve = function () {
        send('/banker/kill/not-believe', {});
    };

    self.bankerPass = function (player) {
        send('/banker/pass', {'player': player});
    };

    self.bankerKill = function (player) {
        send('/banker/kill', {'player': player});
    };

    self.bankerTurnOver = function (player) {
        send('/banker/turn-over', {'player': player});
    };

    self.bankerNotTurnOver = function (player) {
        send('/banker/not-turn-over', {'player': player});
    };

    function send(action, data) {
        data.name = $name;
        stompClient.send(action, {}, JSON.stringify(data));
    }

    self.login = function (name) {
        if (!name) {
            alert('来者何人!');
            return;
        }
        $name = name;

        send('/login', {});

        $('#sp-name').html($name);
        $('#fm-login').remove();
        $('#dv-game').show(1000);
    };

    self.logout = function () {
        send('/logout', {});
        disconnect();
        location.reload();
    };

    function disconnect() {
        if (!stompClient) {
            stompClient.disconnect();
        }
    }

    self.start = function () {
        send('/start', {});
    };

    self.adjust = function () {
        send('/adjust', {});
    };

    self.pass = function () {
        send('/pass', {});
    };

    self.cover = function () {
        send('/cover', {});
    };

    self.force = function () {
        send('/force', {});
    };

    self.open = function () {
        send('/open', {});
    };

    self.win = function () {
        send('/win', {});
    };

    self.lose = function () {
        send('/lose', {});
    };

    self.msg = function (msg) {
        send('/rec', {'msg': msg});
    };

    return self;
})();