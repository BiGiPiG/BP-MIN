<template>
  <div class="message-composer">
    <input
        v-model="localText"
        @keyup.enter="handleSend"
        type="text"
        placeholder="Type your message..."
        class="composer-input"
    />
    <button @click="handleSend" class="send-button">Send</button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'send'])

const localText = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  localText.value = val
})

const handleSend = () => {
  const text = localText.value
  if (text) {
    console.log('text')
    emit('send', text)
    localText.value = ''
    emit('update:modelValue', '')
  }
}
</script>

<style scoped>
.message-composer {
  display: flex;
  padding: 24px 28px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  border-top: 1px solid rgba(102, 126, 234, 0.15);
  gap: 16px;
}

.composer-input {
  flex: 1;
  padding: 18px 22px;
  border: 2px solid rgba(102, 126, 234, 0.25);
  border-radius: 18px;
  outline: none;
  font-size: 16px;
  background: rgba(248, 250, 255, 0.9);
  transition: all 0.3s ease;
  color: #333;
}

.composer-input:focus {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 5px rgba(102, 126, 234, 0.2);
}

.composer-input::placeholder {
  color: #a3b1f1;
  font-weight: 500;
}

.send-button {
  background: linear-gradient(135deg, #667eea, #f093fb);
  color: white;
  border: none;
  border-radius: 18px;
  padding: 18px 28px;
  cursor: pointer;
  font-weight: 700;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
  min-width: 90px;
}

.send-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.45);
  background: linear-gradient(135deg, #f093fb, #667eea);
}

@media (max-width: 768px) {
  .message-composer {
    padding: 20px 16px;
  }
}
</style>