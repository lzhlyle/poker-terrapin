var index = (function () {
    var self = {};
    var $name;
    var $room;
    var _login = false;
    var stompClient = null;

    self.init = function (fn) {
        connect();
        return fn();
    };

    function connect() {
        var socket = new SockJS('/terrapin');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/topic/login', function (response) {
                var data = JSON.parse(response.body);
                if (data.room === $room) {
                    if (!data.message) {
                        _login = false;
                        alert('邀请码错误');
                    } else _login = true;
                }
            });

            stompClient.subscribe('/topic/log', function (response) {
                var data = JSON.parse(response.body);
                if (data.room === $room) {
                    if (data.chat) {
                        appendChat(data.name, data.message);
                    } else {
                        appendLog(data.name, data.message);
                    }
                }
            });

            stompClient.subscribe('/topic/players', function (response) {
                var data = JSON.parse(response.body);
                if (data.room === $room) {
                    refreshPlayers(data.players, data.banker);
                }
            });
        });

        function appendLog(name, txt) {
            if (!txt || !name) return;

            var p = $('<p class="text-center text-log"></p>');
            p.html('【' + name + '】' + txt);
            $('#dv-log').prepend(p);
        }

        function appendChat(name, txt) {
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
            // 当前庄家
            $playerBtns.hide();
            $bankerBtns.show();
        }

        var $dv = $('#dv-players');
        $dv.html('');
        var $tb = $('<table class="table table-hover table-striped terrapin-tb"></table>');
        // name, score, round-score, cards, status/banker-count, operate,
        for (var i = 0, len = players.length; i < len; i++) {
            var $tr = $('<tr></tr>');
            var player = players[i];

            if (player.name === $name) {
                // 当前玩家
                $tr.addClass('success');
            }

            // score
            $tr.append(generateTd(player.score.value));

            // round-score
            $tr.append(generateTd(player.score.curr));

            // name
            $tr.append(generateTd('【' + player.name + '】'));

            // cards
            var cardStr = '';
            if (!!player.handCardStr) {
                if (player.name === $name) {
                    var handCardStr = '您的手牌：' + player.handCardStr;
                    $('#dv-my').html('<h3>' + handCardStr + '</h3>');
                }
                cardStr = isDisplayHandCard(player.status) ? player.handCardStr : '{{?, ?}, {?, ?}}';
            }
            $tr.append(generateTd(cardStr));

            // status
            if (!!banker && banker.name === player.name) {
                // 庄家行
                $tr.append(generateTd(banker.count + '连庄'));
            } else {
                // 闲家行
                $tr.append(generateTd(getStatus(player.status)));
            }

            // operate
            var btns = [];
            if (!banker || banker.name !== player.name) {
                // 闲家行
                if (!!banker && banker.name === $name) {
                    // 庄家看
                    if (player.status === 'PLAYER_PASS') {
                        btns.push(generateBankerBtn('btn-banker-pass', '走过', player.name));
                        btns.push(generateBankerBtn('btn-banker-kill', '杀牌', player.name));
                    } else if (player.status === 'PLAYER_COVER') {
                        btns.push(generateBankerBtn('btn-banker-turnOver', '开牌', player.name));
                        btns.push(generateBankerBtn('btn-banker-notTurnOver', '不开', player.name));
                    }
                } else if (player.name === $name) {
                    // 闲家看
                    if (player.status === 'PLAYER_PASS_BANKER_PASS') {
                        btns.push(generateBankerBtn('btn-player-pass-believe', '信咯', player.name));
                        btns.push(generateBankerBtn('btn-player-pass-not-believe', '不信', player.name));
                    } else if (player.status === 'PLAYER_PASS_BANKER_KILL') {
                        btns.push(generateBankerBtn('btn-player-kill-believe', '信咯', player.name));
                        btns.push(generateBankerBtn('btn-player-kill-not-believe', '不信', player.name));
                    }
                }
            }
            $tr.append(generateTd4Arr(btns));

            if (!!banker && banker.name === player.name) {
                $tr.addClass('terrapin-player-banker');
                $tb.prepend($tr);
            } else $tb.append($tr);
        }// for
        $tb.prepend($('<tr><th>总</th><th>本</th><th></th><th></th><th></th><th></th></tr>'));
        $dv.append($tb);

        bindDynamicBankerBtnEvent();

        function generateTd(html) {
            return $('<td>' + html + '</td>');
        }

        function generateTd4Arr(arr) {
            var $res = $('<td></td>');
            for (var i = 0, len = arr.length; i < len; i++) {
                $res.append(arr[i]);
            }
            return $res;
        }

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
            return $('<button type="button" class="btn btn-warning terrapin-banker-btn" ' +
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
        data.room = $room;
        stompClient.send(action, {}, JSON.stringify(data));
    }

    self.login = function (name, room, code, isPlayer) {
        if (!name) {
            alert('来者何人！');
            return false;
        }
        if (!room) {
            alert('去向何方！');
            return false;
        }
        if (!code) {
            alert('接头暗号！');
            return false;
        }
        $name = name;
        $room = room;

        send('/login', {'code': code, 'isPlayer': isPlayer});

        setTimeout(function () {
            if (_login) {
                $('#sp-name').html($name);
                $('#sp-room').html($room);
                $('#sp-code').html(code);

                $('#dv-mall').remove();
                $('#dv-room').slideDown(500);
                if (isPlayer) $('#dv-game').slideDown(500);
                $('#dv-display').slideDown(500);

                return true; // 已成功加入
            } else return false;
        }, 500);
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
})
();