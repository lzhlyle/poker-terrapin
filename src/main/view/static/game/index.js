var index = (function () {
        var self = {};
        var $name;
        var $room;
        var _login = false;
        var _isPlayer = false;
        var stompClient = null;
        var _notice = [];
        var storage = window.localStorage;

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
                        if (data.name === $name) {
                            if (!data.message) {
                                _login = false;
                                alert('邀请码错误');
                            } else _login = true;
                        }
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
                        refreshObservers(data.observers);
                    }
                });

                stompClient.subscribe('/user/topic/player', function (response) {
                    var data = JSON.parse(response.body);
                    if (data.room === $room) {
                        refreshMyCards(data.players);
                    }
                });
            });
        }

        function appendLog(name, txt) {
            if (!txt || !name) return;

            var p = $('<p class="text-center text-log"></p>');
            p.html('【' + name + '】' + txt);
            $('#dv-log').prepend(p);

            var pl = $('<p class="text-left text-log"></p>');
            pl.html('【' + name + '】' + txt);
            $('#dv-log-only').prepend(pl);
        }

        function appendChat(name, txt) {
            if (!txt || !name) return;

            var p = $('<p class="text-left"></p>');
            if (name === $name) p.addClass('bg-success');
            else p.addClass('bg-warning');
            p.html('【' + name + '】' + txt);
            $('#dv-log').prepend(p);

            var po = $('<p class="text-left"></p>');
            if (name === $name) po.addClass('bg-success');
            else po.addClass('bg-warning');
            po.html('【' + name + '】' + txt);
            $('#dv-chat-only').prepend(po);
        }

        function refreshMyCards(players) {
            if (!players) return;
            for (var i = 0, len = players.length; i < len; i++) {
                var player = players[i];
                if (player.name === $name && !!player.handCardStr) {
                    var handCardStr = '您的手牌：' + player.handCardStr;
                    $('#sp-my').html(handCardStr);
                }
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
                if (banker.status === 'WAITING') {
                    $bankerBtns.removeClass('btn-default').addClass('btn-warning')
                        .removeClass('disabled').prop('disabled', false);
                } else {
                    $bankerBtns.removeClass('btn-warning').addClass('btn-default')
                        .addClass('disabled').prop('disabled', true);
                }
            }

            var $dv = $('#dv-players');
            $dv.html('');
            var $tb = $('<table class="table table-hover table-striped terrapin-tb"></table>');
            var currSum = 0;
            for (var i = 0, len = players.length; i < len; i++) {
                var $tr = $('<tr></tr>');
                var player = players[i];

                if (player.name === $name) {
                    // 当前玩家
                    $tr.addClass('success');
                    if (!banker || player.name !== banker.name) {
                        // 当前闲家
                        if (player.status === 'WAITING') {
                            $playerBtns.removeClass('btn-default').addClass('btn-warning')
                                .removeClass('disabled').prop('disabled', false);
                        } else {
                            $playerBtns.removeClass('btn-warning').addClass('btn-default')
                                .addClass('disabled').prop('disabled', true);
                        }
                    }
                }

                // score
                $tr.append(generateTd(player.score.value));

                // round-score
                currSum += player.score.curr;
                $tr.append(generateTd(player.score.curr));

                // name
                $tr.append(generateTd('【' + player.name + '】'));

                // cards
                var cardStr = '';
                if (!!player.handCardStr) {
                    if (player.name === $name) {
                        var handCardStr = '您的手牌：' + player.handCardStr;
                        $('#sp-my').html(handCardStr);
                    }
                    cardStr = isDisplayHandCard(player.status) ? player.handCardStr : '[?, ?] [?, ?]';
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
                            // notice(player.name, '求走');
                            btns.push(generateBtn('btn-banker-pass', '走过', player.name));
                            btns.push(generateBtn('btn-banker-kill', '杀牌', player.name));
                        } else if (player.status === 'PLAYER_COVER') {
                            // notice(player.name, '盖牌');
                            btns.push(generateBtn('btn-banker-turnOver', '开牌', player.name));
                            btns.push(generateBtn('btn-banker-notTurnOver', '不开', player.name));
                        }
                    } else if (player.name === $name) {
                        // 闲家看
                        if (player.status === 'PLAYER_PASS_BANKER_PASS') {
                            // notice(banker.name, '走过');
                            btns.push(generateBtn('btn-player-pass-believe', '信咯', player.name));
                            btns.push(generateBtn('btn-player-pass-not-believe', '不信', player.name));
                        } else if (player.status === 'PLAYER_PASS_BANKER_KILL') {
                            // notice(banker.name, '杀牌');
                            btns.push(generateBtn('btn-player-kill-believe', '信咯', player.name));
                            btns.push(generateBtn('btn-player-kill-not-believe', '不信', player.name));
                        }
                    }
                }
                $tr.append(generateTd4Arr(btns));

                if (!!banker && banker.name === player.name) {
                    $tr.addClass('terrapin-player-banker');
                    $tb.prepend($tr);
                } else $tb.append($tr);
            }// for
            $tb.prepend($('<tr><th>总</th><th>' + (currSum === 0 ? '√' : currSum) + '</th><th></th><th></th><th></th><th></th></tr>'));
            $dv.append($tb);

            var $btnStart = $('#btn-start');
            if (currSum === 0) $btnStart.removeClass('disabled').prop('disabled', false);
            else $btnStart.addClass('disabled').prop('disabled', true);

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

            function getDynamicBtnClickEvent(id) {
                switch (id) {
                    case 'btn-banker-pass':
                        return index.bankerPass;
                    case 'btn-banker-kill':
                        return index.bankerKill;
                    case 'btn-banker-turnOver':
                        return index.bankerTurnOver;
                    case 'btn-banker-notTurnOver':
                        return index.bankerNotTurnOver;
                    case 'btn-player-pass-believe':
                        return index.bankerPassBelieve;
                    case 'btn-player-pass-not-believe':
                        return index.bankerPassNotBelieve;
                    case 'btn-player-kill-believe':
                        return index.bankerKillBelieve;
                    case 'btn-player-kill-not-believe':
                        return index.bankerKillNotBelieve;
                    default:
                        return function () {
                            return false;
                        };
                }
            }

            function generateBtn(id, txt, player) {
                var $btn = $('<button type="button" class="btn btn-warning terrapin-banker-btn">' + txt + '</button>');

                var fn = getDynamicBtnClickEvent(id);
                $btn.click(function () {
                    fn(player);
                });

                return $btn;
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

        function notice(name, txt) {
            if (window.Notification && Notification.permission !== "denied") {
                Notification.requestPermission(function (status) {
                    if (name !== $name) {
                        _notice.push({name: name, n: new Notification($name, {body: "【" + name + "】" + txt})});
                    }
                });
            }
        }

        function clearNotice() {
            for (var i = 0, len = _notice.length; i < len; i++) {
                var ntc = _notice[i];
                ntc.n.close();
            }
            _notice = [];
        }

        function refreshObservers(observers) {
            var $dvOb = $('#dv-observers');
            $dvOb.html('');
            if (!observers) return;

            var len = observers.length;
            if (len < 1) return;

            var str = '众人皆醉我独醒：';
            for (var i = 0; i < len; i++) {
                str += '@' + observers[i].name + ' ';
            }
            $dvOb.html(str);
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
            _isPlayer = isPlayer;

            send('/login', {'code': code, 'isPlayer': _isPlayer});

            setTimeout(function () {
                if (_login) {
                    $('#sp-name').html($name);
                    $('#sp-room').html($room);
                    $('#sp-code').html(code);

                    $('#dv-mall').remove();
                    $('#dv-room').slideDown(500);
                    if (isPlayer) $('#dv-game').slideDown(500);
                    $('#dv-display').slideDown(500);

                    storage.name = name;
                    storage.room = room;
                    storage.code = code;
                    storage.isPlayer = isPlayer;

                    return true; // 已成功加入
                } else return false;
            }, 500);
        };

        self.logout = function () {
            send('/logout', {'isPlayer': _isPlayer});
            disconnect();
            storage.clear();
            location.reload();
        };

        self.relogin = function () {
            if (storage.name != null) {
                var ok = self.login(storage.name, storage.room, storage.code, storage.isPlayer);
                return ok
            }
            return false;
        }

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
    }

)
();