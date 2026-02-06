<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

// ─────────────────────────────────────
// Setup
// ─────────────────────────────────────

const router = useRouter()

// Массив всех градиентов для отображения
const GRADIENTS = [
  'linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%)',
  'linear-gradient(135deg, #8b5cf6 0%, #c4b5fd 100%)',
  'linear-gradient(135deg, #a855f7 0%, #d8b4fe 100%)',
  'linear-gradient(135deg, #d946ef 0%, #f0abfc 100%)',
  'linear-gradient(135deg, #f97316 0%, #fdba74 100%)',
  'linear-gradient(135deg, #ea580c 0%, #fed7aa 100%)',
  'linear-gradient(135deg, #7e4aff 0%, #f97316 100%)',
  'linear-gradient(135deg, #a855f7 0%, #ea580c 100%)'
]

// Маппинг градиентов в имена констант enum
const gradientToEnumMap = {
  'linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%)': 'VIOLET_GRADIENT',
  'linear-gradient(135deg, #8b5cf6 0%, #c4b5fd 100%)': 'INDIGO_GRADIENT',
  'linear-gradient(135deg, #a855f7 0%, #d8b4fe 100%)': 'FUCHSIA_GRADIENT',
  'linear-gradient(135deg, #d946ef 0%, #f0abfc 100%)': 'PINK_GRADIENT',
  'linear-gradient(135deg, #f97316 0%, #fdba74 100%)': 'ORANGE_GRADIENT',
  'linear-gradient(135deg, #ea580c 0%, #fed7aa 100%)': 'AMBER_GRADIENT',
  'linear-gradient(135deg, #7e4aff 0%, #f97316 100%)': 'BICOLOR_VIOLET_ORANGE',
  'linear-gradient(135deg, #a855f7 0%, #ea580c 100%)': 'BICOLOR_FUCHSIA_AMBER'
}

// Обратное маппирование: из имени константы в строку градиента
const enumToGradientMap = Object.fromEntries(
    Object.entries(gradientToEnumMap).map(([gradient, enumName]) => [enumName, gradient])
)

// ─────────────────────────────────────
// Reactive State
// ─────────────────────────────────────

const profileData = ref({
  username: '',
  nickname: '',
  bio: '',
  birthDate: '',
  avatarStyle: GRADIENTS[0] // Значение по умолчанию
})

let initialData = {}

// ─────────────────────────────────────
// Computed Properties
// ─────────────────────────────────────

const profileInitial = computed(() => {
  return profileData.value.username.charAt(0).toUpperCase() || '?'
})

// ─────────────────────────────────────
// Lifecycle Hooks
// ─────────────────────────────────────

onMounted(async () => {
  try {
    const response = await fetch(`/api/profiles/${localStorage.getItem('username')}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()
    console.log('Полученные данные профиля:', data)

    const gradient = enumToGradientMap[data.profileColor] || GRADIENTS[0]

    profileData.value = {
      username: data.username || '',
      nickname: data.nickname || '',
      bio: data.bio || '',
      birthDate: data.birthDate
          ? new Date(data.birthDate).toISOString().split('T')[0]
          : '',
      avatarStyle: gradient
    }

    initialData = { ...profileData.value }
    console.log('Инициализированные данные:', profileData.value)
  } catch (error) {
    console.error('Ошибка загрузки профиля:', error)
  }
})

// ─────────────────────────────────────
// Methods
// ─────────────────────────────────────

const selectAvatarColor = (gradient) => {
  console.log('Выбран градиент:', gradient)
  profileData.value.avatarStyle = gradient
}

const saveChanges = async () => {
  const { nickname, username, bio, birthDate, avatarStyle } = profileData.value

  console.log('Строка градиента:', avatarStyle)

  // Преобразуем строку градиента в имя константы для отправки на бэкенд
  const profileColorEnum = gradientToEnumMap[avatarStyle] || 'VIOLET_GRADIENT'
  console.log('Имя константы для отправки:', profileColorEnum)

  const requestBody = {
    nickname,
    username,
    birthDate,
    bio,
    profileColor: profileColorEnum
  }

  console.log('Отправляемые данные:', requestBody)

  try {
    const response = await fetch(`/api/profiles/${localStorage.getItem('username')}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestBody)
    })

    console.log('Статус ответа:', response.status)

    if (!response.ok) {
      const errorText = await response.text()
      console.error('Ошибка HTTP:', response.status, errorText)
      alert(`Ошибка при сохранении: ${response.status}`)
      return
    }

    const responseData = await response.json()
    console.log('Успешный ответ:', responseData)

    initialData = { ...profileData.value }
    alert('Профиль успешно сохранён!')
  } catch (error) {
    console.error('Ошибка при сохранении профиля:', error)
    alert('Произошла ошибка при сохранении профиля')
  }
}

const discardChanges = () => {
  console.log('Отмена изменений')
  profileData.value = { ...initialData }
}

const goBack = () => {
  router.go(-1)
}
</script>

<template>
  <div class="user-profile-outer">
    <div class="user-profile-container">
      <!-- Header -->
      <div class="profile-header">
        <button class="back-button" @click="goBack" aria-label="Back">
          ←
        </button>
        <h1 class="profile-title">Your profile</h1>
      </div>

      <!-- Avatar with Color Picker -->
      <div class="avatar-section">
        <div class="avatar-large" :style="{ background: profileData.avatarStyle }">
          {{ profileInitial }}
        </div>

        <!-- Color Picker Section -->
        <div class="color-picker-container">
          <label class="color-picker-label">Цвет аватара</label>
          <div class="color-options">
            <button
                v-for="(gradient, index) in GRADIENTS"
                :key="index"
                class="color-option"
                :style="{ background: gradient }"
                :class="{ selected: profileData.avatarStyle === gradient }"
                @click="selectAvatarColor(gradient)"
                :aria-label="`Выбрать цвет ${index + 1}`"
            >
              <div v-if="profileData.avatarStyle === gradient" class="selected-indicator">
                ✓
              </div>
            </button>
          </div>
        </div>
      </div>

      <!-- Profile Fields -->
      <div class="fields-grid">
        <div class="field">
          <label class="field-label">Имя пользователя</label>
          <input
              v-model="profileData.nickname"
              type="text"
              class="field-input"
              placeholder="Введите имя"
          />
        </div>

        <div class="field">
          <label class="field-label">ID пользователя</label>
          <input
              v-model="profileData.username"
              type="text"
              class="field-input"
              placeholder="Введите ID"
              readonly
              disabled
          />
        </div>

        <div class="field full-width">
          <label class="field-label">Дата рождения</label>
          <input
              v-model="profileData.birthDate"
              type="date"
              class="field-input"
          />
        </div>

        <div class="field full-width">
          <label class="field-label">О себе</label>
          <textarea
              v-model="profileData.bio"
              class="field-textarea"
              placeholder="Расскажите о себе..."
              rows="5"
          ></textarea>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="actions">
        <button class="btn btn-secondary" @click="discardChanges">
          Отменить
        </button>
        <button class="btn btn-primary" @click="saveChanges">
          Сохранить изменения
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ─────────────────────────────────────
   Outer Container
   ───────────────────────────────────── */
.user-profile-outer {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100dvh;
  padding: 20px;
  box-sizing: border-box;
}

/* ─────────────────────────────────────
   Profile Container
   ───────────────────────────────────── */
.user-profile-container {
  width: 100%;
  max-width: 760px;
  min-width: 300px;
  padding: 48px 40px;
  background: white;
  border-radius: 24px;
  box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.12),
      0 8px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
  display: flex;
  flex-direction: column;
  gap: 32px;
  box-sizing: border-box;
  overflow-y: auto;
  max-height: 100%;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.user-profile-container::-webkit-scrollbar {
  display: none;
}

/* ─────────────────────────────────────
   Layout Sections
   ───────────────────────────────────── */
.profile-header,
.avatar-section,
.fields-grid,
.actions {
  flex-shrink: 0;
}

/* ─────────────────────────────────────
   Back Button
   ───────────────────────────────────── */
.back-button {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: #f0ebff;
  border: none;
  font-size: 20px;
  color: #7e4aff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.back-button:hover {
  background: #e0d6ff;
  transform: translateX(-2px);
}

/* ─────────────────────────────────────
   Profile Title
   ───────────────────────────────────── */
.profile-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  line-height: 1.2;
  text-align: center;
}

/* ─────────────────────────────────────
   Avatar Section
   ───────────────────────────────────── */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 8px 0;
  gap: 20px;
  flex-shrink: 0;
}

.avatar-large {
  width: 120px;
  height: 120px;
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 42px;
  box-shadow:
      0 0 0 4px rgba(255, 255, 255, 0.3),
      0 12px 40px rgba(126, 74, 255, 0.35),
      0 6px 20px rgba(249, 115, 22, 0.25);
  transition:
      transform 0.4s cubic-bezier(0.4, 0, 0.2, 1),
      box-shadow 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.avatar-large::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 29px;
  background: linear-gradient(
      45deg,
      rgba(255, 255, 255, 0.4),
      rgba(255, 255, 255, 0),
      rgba(255, 255, 255, 0.3)
  );
  z-index: -1;
  opacity: 0.3;
  pointer-events: none;
}

.avatar-large:hover {
  transform: scale(1.08);
  box-shadow:
      0 0 0 4px rgba(255, 255, 255, 0.4),
      0 18px 55px rgba(126, 74, 255, 0.5),
      0 10px 30px rgba(249, 115, 22, 0.4);
}

.avatar-large:hover::before {
  opacity: 0.5;
}

/* ─────────────────────────────────────
   Color Picker Styles
   ───────────────────────────────────── */
.color-picker-container {
  text-align: center;
  width: 100%;
  max-width: 400px;
}

.color-picker-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 12px;
  text-align: left;
}

.color-options {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 16px;
  border: 1px solid #e8e8e8;
}

.color-option {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 2px solid transparent;
  background-clip: padding-box;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  transition: all 0.2s ease;
  position: relative;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.color-option:hover {
  transform: scale(1.15);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  z-index: 10;
}

.color-option.selected {
  transform: scale(1.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
  z-index: 20;
  border-color: white;
}

.color-option::before {
  content: '';
  position: absolute;
  inset: -3px;
  border-radius: 50%;
  background: linear-gradient(
      45deg,
      #7e4aff,
      #8b5cf6,
      #a855f7,
      #d946ef,
      #f97316,
      #ea580c
  );
  z-index: -1;
  opacity: 0.6;
}

.selected-indicator {
  position: absolute;
  font-size: 20px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  animation: popIn 0.3s ease-out;
}

@keyframes popIn {
  0% {
    transform: scale(0.5);
    opacity: 0;
  }
  70% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* ─────────────────────────────────────
   Form Fields Grid
   ───────────────────────────────────── */
.fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
  margin: 8px 0;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field.full-width {
  grid-column: span 2;
}

.field-label {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  padding-left: 4px;
}

.field-input,
.field-textarea {
  width: 100%;
  padding: 16px 20px;
  border: 2px solid #e8e8e8;
  border-radius: 16px;
  font-size: 15px;
  font-family: inherit;
  outline: none;
  background: #f8f9fa;
  transition: all 0.25s ease;
  box-sizing: border-box;
}

.field-input:focus,
.field-textarea:focus {
  border-color: #7e4aff;
  background: white;
  box-shadow: 0 0 0 4px rgba(126, 74, 255, 0.15);
}

.field-input:disabled {
  background-color: #f0f0f0;
  color: #888;
  cursor: not-allowed;
}

/* ─────────────────────────────────────
   Textarea Specific
   ───────────────────────────────────── */
.field-textarea {
  min-height: 120px;
  max-height: 120px;
  resize: none;
  overflow-y: auto;
  line-height: 1.5;
}

.field-textarea::-webkit-scrollbar {
  width: 6px;
}

.field-textarea::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.field-textarea::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.field-textarea::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* ─────────────────────────────────────
   Action Buttons
   ───────────────────────────────────── */
.actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.btn {
  flex: 1;
  padding: 16px 24px;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  min-height: 52px;
}

.btn-primary {
  background: linear-gradient(135deg, #7e4aff, #a78bfa);
  color: white;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #6a2c91, #8b5cf6);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(126, 74, 255, 0.3);
}

.btn-secondary {
  background: #f5f5f5;
  color: #444;
}

.btn-secondary:hover {
  background: #e8e8e8;
  transform: translateY(-2px);
}

/* ─────────────────────────────────────
   Responsive Design
   ───────────────────────────────────── */
@media (max-width: 768px) {
  .user-profile-outer {
    padding: 16px;
  }

  .user-profile-container {
    padding: 36px 28px;
    gap: 28px;
    border-radius: 20px;
  }

  .fields-grid {
    grid-template-columns: 1fr;
    gap: 22px;
  }

  .field.full-width {
    grid-column: span 1;
  }

  .profile-title {
    font-size: 24px;
  }

  .avatar-large {
    width: 100px;
    height: 100px;
    font-size: 36px;
    border-radius: 24px;
  }

  .avatar-large::before {
    inset: -5px;
    border-radius: 29px;
  }

  .color-options {
    gap: 8px;
  }

  .color-option {
    width: 38px;
    height: 38px;
  }

  .color-option::before {
    inset: -2px;
  }

  .field-textarea {
    min-height: 100px;
    max-height: 100px;
  }

  .actions {
    flex-direction: column;
    gap: 12px;
  }

  .btn {
    min-height: 48px;
  }
}

@media (max-width: 480px) {
  .user-profile-container {
    padding: 28px 22px;
    gap: 24px;
    border-radius: 18px;
  }

  .profile-header {
    gap: 12px;
  }

  .back-button {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }

  .profile-title {
    font-size: 22px;
  }

  .avatar-large {
    width: 90px;
    height: 90px;
    font-size: 32px;
    border-radius: 22px;
  }

  .avatar-large::before {
    inset: -4px;
    border-radius: 26px;
  }

  .color-options {
    gap: 6px;
  }

  .color-option {
    width: 34px;
    height: 34px;
  }

  .color-option::before {
    inset: -2px;
  }

  .selected-indicator {
    font-size: 16px;
  }

  .field-input,
  .field-textarea {
    padding: 14px 18px;
  }

  .field-textarea {
    min-height: 90px;
    max-height: 90px;
  }
}
</style>