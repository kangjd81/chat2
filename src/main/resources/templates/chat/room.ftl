<!doctype html>
<html lang="en">
  <head>
    <title>Websocket Chat</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <!-- CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.min.css">
    <style>
      [v-cloak] {
          display: none;
      }
    </style>
  </head>
  <body>
    <div class="container" id="app" v-cloak>
        <div class="row">
            <div class="col-md-6">
                <h3>채팅방 리스트</h3>

                <#--<div class="input-group">
                    <div class="input-group-prepend">
                        <label class="input-group-text">방제목</label>
                    </div>
                    <input type="text" class="form-control" v-model="room_name" v-on:keyup.enter="createRoom">
                    <div class="input-group-append">
                        <button class="btn btn-primary" type="button" @click="createRoom">채팅방 개설</button>
                    </div>
                </div>-->
                <ul class="list-group">
                    <li class="list-group-item list-group-item-action" v-for="item in chatrooms" v-bind:key="item.roomId" v-on:click="enterRoom(item.roomId, item.name)">
                        <h6>{{item.name}} <span class="badge badge-info badge-pill">{{item.notReadCount}}</span></h6>
                    </li>
                </ul>
            </div>


            <div class="col-md-6 text-right">
                <a class="btn btn-primary btn-sm" href="/logout">로그아웃</a>

                <div class="row">
                   대화창
                </div>
                <div class="input-group">
                    <div class="input-group-prepend">
                        <label class="input-group-text">내용</label>
                    </div>
                    <input type="text" class="form-control" v-model="message" v-on:keypress.enter="sendMessage('TALK')">
                    <div class="input-group-append">
                        <button class="btn btn-primary" type="button" @click="sendMessage('TALK')">보내기</button>
                    </div>
                </div>
                <ul class="list-group">
                    <li class="list-group-item" v-for="message in messages">
                        [{{message.type}}] {{message.sender}} : {{message.message}}
                    </li>
                </ul>

            </div>
        </div>


    </div>


    <!-- JavaScript -->
    <script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
    <script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
    <script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>

    <script>
        // websocket & stomp initialize
        var sock = new SockJS("/ws-stomp");
        var ws = Stomp.over(sock);
        // vue.js
        var vm = new Vue({
            el: '#app',
            data: {
                room_name : '',
                chatrooms: [],

                roomId: '',
                roomName: '',
                message: '',
                messages: [],
                token: '',
                notReadCount: 0
            },
            created() {
                this.findAllRoom();

                axios.get('/chat/user').then(response => {
                    this.token = response.data.token;

                    ws.connect({"token":this.token}, function(frame) {
                        console.log('Connected: ' + frame);
                    }, function(error) {
                        alert("서버 연결에 실패 하였습니다. 다시 접속해 주십시요.");
                    });
                });

            },
            methods: {
                findAllRoom: function() {
                    axios.get('/chat/rooms').then(response => {
                        // prevent html, allow json array
                        if(Object.prototype.toString.call(response.data) === "[object Array]")
                            this.chatrooms = response.data;
                    });
                },
                getRoomMessage: function(roomId) {
                    axios.get('/chat/room/'+ roomId).then(response => {
                        // prevent html, allow json array
                        if(Object.prototype.toString.call(response.data) === "[object Array]"){
                            this.messages = response.data;
                            //this.messages.unshift({"type":recv.type,"sender":recv.sender,"message":recv.message})
                            //this.messages.unshift({"type":"test1","sender":"test2","message":"test3"})
                        }
                    });
                },
                createRoom: function() {
                    if("" === this.room_name) {
                        alert("방 제목을 입력해 주십시요.");
                        return;
                    } else {
                        var params = new URLSearchParams();
                        params.append("name",this.room_name);
                        axios.post('/chat/room', params)
                        .then(
                            response => {
                                alert(response.data.name+"방 개설에 성공하였습니다.")
                                this.room_name = '';
                                this.findAllRoom();
                            }
                        )
                        .catch( response => { alert("채팅방 개설에 실패하였습니다."); } );
                    }
                },
                enterRoom: function(roomId, roomName) {
                    this.roomId = roomId;
                    this.roomName = roomName;

                    var _this = this;
                    // 대화 내역 가져오기
                    _this.getRoomMessage(roomId);

                    var sub = ws.subscribe("/sub/chat/room/"+_this.roomId, function(message) {
                        var recv = JSON.parse(message.body);
                        _this.recvMessage(recv);
                    });

                    // 구독 취소 (테스트)
                    sub.unsubscribe();
                },
                sendMessage: function(type) {
                    ws.send("/pub/chat/message", {"token":this.token}, JSON.stringify({type:type, roomId:this.roomId, message:this.message}));
                    this.message = '';
                },
                recvMessage: function(recv) {
                    console.log("recvMessage : {}", recv);
                    this.messages.unshift({"type":recv.type,"sender":recv.sender,"message":recv.message})
                }
            }
        });
    </script>
  </body>
</html>