<script setup>
import { ref, defineEmits } from 'vue'

const emit = defineEmits(['go-back'])

const props = defineProps({
  activeChatName: String,
  activeChat: Object
})

let displayMessages = [
  {
    sender: "BiGPiG",
    text: "Hi, bro)",
    time: "10:30"
  }
]

const username = localStorage.getItem('username');

function sendMessage() {
  console.log("send");
}

</script>

<template>
  <div class="chat-container">
    <div class="chat-header">
      <button @click="$emit('go-back')" class="back-btn">←</button>
      <div class="chat-title">{{ activeChatName }}</div>
    </div>

    <div class="messages-wrapper">
      <div v-for="(msg, index) in displayMessages" :key="index" class="message-row">
        <div :class="msg.sender === username ? 'message sent' : 'message received'">
          <div class="message-text">{{ msg.text }}</div>
          <div class="message-time">{{ msg.time }}</div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <input type="text" placeholder="Type a message..." class="message-input" />
      <button @click="sendMessage" class="send-btn">Send</button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  background: linear-gradient(135deg, #faf8ff 0%, #fff7ed 100%);
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;
  position: relative;
}

.chat-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #7e4aff, #f97316, transparent);
}

.chat-header {
  display: flex;
  align-items: center;
  padding-left: 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(126, 74, 255, 0.1);
  box-shadow: 0 2px 20px rgba(126, 74, 255, 0.1);
  height: 50px;
}

.back-btn {
  background: linear-gradient(135deg, #7e4aff, #a78bfa);
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: white;
  margin-right: 16px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(126, 74, 255, 0.3);
}

.back-btn:hover {
  background: linear-gradient(135deg, #f97316, #fdba74);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(249, 115, 22, 0.4);
}

.chat-title {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #7e4aff, #f97316);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.messages-wrapper {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background:
      radial-gradient(circle at 20% 80%, rgba(126, 74, 255, 0.05) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(249, 115, 22, 0.05) 0%, transparent 50%);
}

.message-row {
  display: flex;
  width: 100%;
}

.message-row .sent {
  margin-left: auto;
  justify-content: flex-end;
}

.message-row .received {
  margin-right: auto;
  justify-content: flex-start;
}

.message {
  max-width: 70%;
  padding: 16px 20px;
  border-radius: 20px;
  position: relative;
  word-break: break-word;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.message:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.message.received {
  background: linear-gradient(135deg, #ffffff, #f8f7ff);
  border: 1px solid rgba(126, 74, 255, 0.1);
  border-bottom-left-radius: 6px;
  border-top-left-radius: 6px;
}

.message.received::before {
  content: '';
  position: absolute;
  left: -8px;
  top: 0;
  width: 8px;
  height: 100%;
  background: linear-gradient(180deg, #7e4aff, #a78bfa);
  border-radius: 4px 0 0 4px;
}

.message.sent {
  background: linear-gradient(135deg, #7e4aff, #f97316);
  color: white;
  border-bottom-right-radius: 6px;
  border-top-right-radius: 6px;
  box-shadow: 0 4px 20px rgba(126, 74, 255, 0.3);
}

.message.sent::before {
  content: '';
  position: absolute;
  right: -8px;
  top: 0;
  width: 8px;
  height: 100%;
  background: linear-gradient(180deg, #f97316, #fdba74);
  border-radius: 0 4px 4px 0;
}

.message-text {
  font-size: 15px;
  line-height: 1.4;
  margin-bottom: 4px;
}

.message-time {
  font-size: 11px;
  opacity: 0.8;
  margin-top: 6px;
  text-align: right;
  font-weight: 500;
}

.message.received .message-time {
  color: #8b5cf6;
}

.message.sent .message-time {
  color: rgba(255, 255, 255, 0.9);
}

.input-area {
  display: flex;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(126, 74, 255, 0.1);
  gap: 12px;
}

.message-input {
  flex: 1;
  padding: 16px 20px;
  border: 2px solid rgba(126, 74, 255, 0.2);
  border-radius: 16px;
  outline: none;
  font-size: 15px;
  background: rgba(248, 247, 255, 0.8);
  transition: all 0.3s ease;
  color: #333;
}

.message-input:focus {
  border-color: #7e4aff;
  background: white;
  box-shadow: 0 0 0 4px rgba(126, 74, 255, 0.15);
}

.message-input::placeholder {
  color: #a78bfa;
}

.send-btn {
  background: linear-gradient(135deg, #7e4aff, #f97316);
  color: white;
  border: none;
  border-radius: 16px;
  padding: 16px 24px;
  cursor: pointer;
  font-weight: 700;
  font-size: 15px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(126, 74, 255, 0.3);
  min-width: 80px;
}

.send-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(126, 74, 255, 0.4);
  background: linear-gradient(135deg, #f97316, #7e4aff);
}

/* Стили для скроллбара */
.messages-wrapper::-webkit-scrollbar {
  width: 6px;
}

.messages-wrapper::-webkit-scrollbar-track {
  background: rgba(126, 74, 255, 0.05);
  border-radius: 3px;
}

.messages-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #7e4aff, #f97316);
  border-radius: 3px;
}

.messages-wrapper::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #f97316, #7e4aff);
}

/* Анимации */
.message {
  animation: messageAppear 0.3s ease-out;
}

@keyframes messageAppear {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .chat-container {
    border-radius: 0;
  }

  .chat-header {
    padding: 16px 20px;
  }

  .messages-wrapper {
    padding: 16px 20px;
  }

  .input-area {
    padding: 16px 20px;
  }

  .message {
    max-width: 85%;
    padding: 14px 18px;
  }
}
</style>