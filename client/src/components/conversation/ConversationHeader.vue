<template>
  <div class="conversation-header">
    <button @click="onBack" class="navigation-button">←</button>
    <div class="contact-info">
      <div class="contact-name" @click="onOpenProfile">{{ contactName }}</div>
      <div class="status-wrapper">
        <span
            class="status-indicator"
            :class="statusClass"
        ></span>
        <span class="status-text">{{ statusText }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  contactName: { type: String, required: true },
  interlocutorStatus: {
    type: String,
    default: 'ONLINE'
  }
})

const emit = defineEmits(['back', 'open-profile'])

const onBack = () => emit('back')
const onOpenProfile = () => emit('open-profile')

const statusClass = computed(() => {
  return props.interlocutorStatus?.toLowerCase() || 'offline'
})

const statusText = computed(() => {
  const statusMap = {
    ONLINE: 'online',
    OFFLINE: 'offline',
    AWAY: 'away',
    BUSY: 'busy'
  }
  return statusMap[props.interlocutorStatus] || 'offline'
})
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

.contact-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-name {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #f093fb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  cursor: pointer;
  line-height: 1.2;
}

.status-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.status-indicator {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  position: relative;
  box-shadow: 0 0 0 2px white;
}

.status-indicator.online {
  background-color: #4CAF50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
  animation: pulse 2s infinite;
}

.status-indicator.offline {
  background-color: #9E9E9E;
}

.status-indicator.away {
  background-color: #FFC107;
  box-shadow: 0 0 0 2px rgba(255, 193, 7, 0.2);
}

.status-indicator.busy {
  background-color: #F44336;
  box-shadow: 0 0 0 2px rgba(244, 67, 54, 0.2);
}

.status-text {
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.4),
    0 0 0 2px rgba(76, 175, 80, 0.2);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(76, 175, 80, 0),
    0 0 0 2px rgba(76, 175, 80, 0.2);
  }
}
</style>