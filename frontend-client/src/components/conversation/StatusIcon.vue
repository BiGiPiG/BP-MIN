<template>
  <div
      class="status-icon"
      :class="[status, { 'outgoing': isOutgoing }]"
      :title="statusTitles[status]"
  >
    <!-- Отправка: анимированный круг -->
    <svg v-if="status === 'sending'" viewBox="0 0 24 24" class="icon sending">
      <circle
          cx="12"
          cy="12"
          r="9"
          stroke="currentColor"
          stroke-width="2"
          fill="none"
          stroke-dasharray="30 70"
          stroke-linecap="round"
      >
        <animateTransform
            attributeName="transform"
            type="rotate"
            from="0 12 12"
            to="360 12 12"
            dur="1.4s"
            repeatCount="indefinite"
        />
      </circle>
    </svg>

    <!-- Доставлено: одна галочка -->
    <svg v-else-if="status === 'delivered'" viewBox="0 0 24 24" class="icon delivered">
      <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
    </svg>

    <!-- Прочитано: одна галочка в синем кружке -->
    <svg v-else-if="status === 'read'" viewBox="0 0 24 24" class="icon read">
      <circle cx="12" cy="12" r="11" fill="currentColor" opacity="0.15"/>
      <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
    </svg>
  </div>
</template>

<script setup>
const props = defineProps({
  status: {
    type: String,
    required: true,
    validator: (value) => ['sending', 'delivered', 'read'].includes(value)
  },
  isOutgoing: {
    type: Boolean,
    default: false
  }
})

const statusTitles = {
  sending: 'Отправка...',
  delivered: 'Доставлено',
  read: 'Прочитано'
}
</script>

<style scoped>
.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  min-width: 18px;
  transition: transform 0.25s ease;
}

.status-icon:hover {
  transform: scale(1.2);
}

.icon {
  width: 16px;
  height: 16px;
  display: block;
}

/* Отправка: оранжевая анимация */
.status-icon.sending .icon {
  color: #FF9800;
  animation: pulse 1.8s ease-in-out infinite;
}

/* Доставлено: полупрозрачная белая галочка для исходящих */
.status-icon.outgoing.delivered .icon {
  color: rgba(255, 255, 255, 0.72);
}

/* Доставлено: серая галочка для входящих */
.status-icon:not(.outgoing).delivered .icon {
  color: #9E9E9E;
}

/* Прочитано: синий кружок с галочкой */
.status-icon.read .icon {
  color: #4FC3F7; /* Голубой цвет */
}

/* Пульсация для отправки */
@keyframes pulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

/* Адаптивность */
@media (max-width: 768px) {
  .status-icon {
    width: 16px;
    height: 16px;
    min-width: 16px;
  }
  .icon {
    width: 14px;
    height: 14px;
  }
}
</style>