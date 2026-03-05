<template>
  <div class="conversation-placeholder">
    <div class="animated-background">
      <div
          v-for="element in floatingElements"
          :key="element.id"
          class="floating-dot"
          :style="{
          top: `${element.positionY}%`,
          left: `${element.positionX}%`,
          width: `${element.dimension}px`,
          height: `${element.dimension}px`,
          animationDelay: `${element.startDelay}s`,
          animationDuration: `${element.animationLength}s`
        }"
      ></div>
    </div>

    <div class="placeholder-content">
      <div class="placeholder-card">
        <h2>Select a conversation to start chatting</h2>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const floatingElements = ref([])

onMounted(() => {
  const elementsArray = []
  for (let i = 0; i < 25; i++) {
    elementsArray.push({
      id: i,
      positionY: Math.random() * 100,
      positionX: Math.random() * 100,
      dimension: 8 + (i % 12),
      startDelay: (i % 5) * 0.8,
      animationLength: 3 + (i % 4)
    })
  }
  floatingElements.value = elementsArray
})
</script>

<style scoped>
.conversation-placeholder {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea, #764ba2);
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.animated-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  overflow: hidden;
}

.floating-dot {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: gentle-float 5s infinite ease-in-out;
  z-index: 2;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.6);
}

@keyframes gentle-float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.6;
  }
  25% {
    transform: translate(12px, -18px) scale(1.15);
    opacity: 1;
  }
  50% {
    transform: translate(-8px, 12px) scale(0.85);
    opacity: 0.7;
  }
  75% {
    transform: translate(18px, 8px) scale(1.08);
    opacity: 0.9;
  }
}

.placeholder-content {
  position: relative;
  z-index: 3;
  text-align: center;
  color: white;
  padding: 0 24px;
}

.placeholder-card {
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(16px);
  border-radius: 24px;
  padding: 40px 32px;
  max-width: 520px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.placeholder-card h2 {
  font-size: 26px;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
</style>