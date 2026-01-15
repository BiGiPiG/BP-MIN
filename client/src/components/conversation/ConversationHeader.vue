<template>
  <div class="conversation-header">
    <button @click="onBack" class="navigation-button">←</button>
    <div class="contact-name" @click="onOpenProfile">{{ contactName }}</div>
  </div>
</template>

<script setup>
defineProps({
  contactName: { type: String, required: true }
})

const emit = defineEmits(['back', 'open-profile'])

const onBack = () => emit('back')
const onOpenProfile = () => emit('open-profile')
</script>

<style scoped>
.conversation-header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(102, 126, 234, 0.15);
  box-shadow: 0 2px 24px rgba(102, 126, 234, 0.12);
  height: 68px;
  z-index: 10;
}

.navigation-button {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: white;
  margin-right: 20px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35);
}

.navigation-button:hover {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 24px rgba(245, 87, 108, 0.45);
}

.contact-name {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #f093fb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  cursor: pointer;
}
</style>