<script setup>
import {onMounted, ref} from 'vue'

const email = ref('')
const password = ref('')
const username = ref('')
const confirmPassword = ref('')
const showLogin = ref(true)

const particles = ref([])

onMounted(() => {
  const arr = []
  for (let i = 0; i < 25; i++) {
    arr.push({
      id: i,
      top: Math.random() * 100,
      left: Math.random() * 100,
      size: 8 + (i % 12),
      delay: (i % 5) * 0.8,
      duration: 3 + (i % 4)
    })
  }
  particles.value = arr
})

const toggleForm = () => {
  showLogin.value = !showLogin.value
}

const handleSignin = () => {
  console.log('Login:', { email: email.value, password: password.value })
}

const handleSignup = () => {
  console.log('Signup:', { username: username.value, password: password.value })
  fetch("http://localhost:8080/api/v1/signin",
      {
        method: "POST",
        body: JSON
            .stringify
            ({
              username: username.value,
              email: email.value,
              password: password.value
            }),
        headers: {
          "Content-type": "application/json",
        },
      })
      .then((response) => response.json())
      .then((json) => console.log(json));
}
</script>

<template>
  <div class="login-container">
    <div class="background">
      <div
          v-for="p in particles"
          :key="p.id"
          class="particle"
          :style="{
            top: `${p.top}%`,
            left: `${p.left}%`,
            width: `${p.size}px`,
            height: `${p.size}px`,
            animationDelay: `${p.delay}s`,
            animationDuration: `${p.duration}s`
          }"
      ></div>
    </div>

    <div class="form-container">
      <div class="form-card login-card" :class="{ active: showLogin }">
        <h2>BP MIN</h2>
        <div class="input-group">
          <span class="icon">✉️</span>
          <input type="email" v-model="email" placeholder="email" required />
        </div>
        <div class="input-group password-input">
          <span class="icon">🔒</span>
          <input type="password" v-model="password" placeholder="password" required />
        </div>
        <button @click="handleSignin" class="sign-in-btn">Sign In</button>
        <p class="switch-form">
          Create Account? <span @click="toggleForm" class="link">Sign Up</span>
        </p>
      </div>

      <div class="form-card Signup-card" :class="{ active: !showLogin }">
        <h2>SIGN UP</h2>
        <div class="input-group">
          <span class="icon">👤</span>
          <input type="text" v-model="username" placeholder="username" required />
        </div>
        <div class="input-group">
          <span class="icon">🔑</span>
          <input type="password" v-model="password" placeholder="password" required />
        </div>
        <div class="input-group">
          <span class="icon">🔄</span>
          <input type="password" v-model="confirmPassword" placeholder="confirm password" required />
        </div>
        <button @click="handleSignup" class="Signup-btn">Sign Up</button>
        <p class="switch-form">
          Already have account? <span @click="toggleForm" class="link">Sign In</span>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
*, *::before, *::after {
  box-sizing: inherit;
}

.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', system-ui, sans-serif;
  background: linear-gradient(135deg, #8e44ad, #6a3093);
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  animation: float 5s infinite ease-in-out;
  z-index: 2;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.7;
  }
  25% {
    transform: translate(10px, -15px) scale(1.1);
    opacity: 1;
  }
  50% {
    transform: translate(-5px, 10px) scale(0.9);
    opacity: 0.8;
  }
  75% {
    transform: translate(15px, 5px) scale(1.05);
    opacity: 0.9;
  }
}

@keyframes wave-animation {
  0% {
    transform: translateX(0) translateZ(0) scaleY(1);
  }
  50% {
    transform: translateX(-25%) translateZ(0) scaleY(0.8);
  }
  100% {
    transform: translateX(-50%) translateZ(0) scaleY(1);
  }
}

.form-container {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
  position: relative;
  z-index: 3;
}

.form-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.4s ease;
  width: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.form-card:not(.active) {
  position: absolute;
  opacity: 0;
  transform: translateY(20px);
  pointer-events: none;
}

.form-card.active {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
  text-align: center;
  font-weight: bold;
  font-family: "Raleway ExtraBold",serif;
}

.input-group {
  position: relative;
  margin-bottom: 20px;
  width: 100%;
}

.input-group .icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
  pointer-events: none;
  font-size: 16px;
  z-index: 2;
}

.input-group input {
  width: 100%;
  padding: 12px 15px 12px 48px;
  border: 1px solid #ddd;
  border-radius: 15px;
  font-size: 14px;
  background: #fafafa;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
  outline: none;
  color: #333;
  font-family: inherit;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.input-group input::placeholder {
  color: #aaa;
  opacity: 1;
}

.input-group input:focus {
  border-color: #8e44ad;
  box-shadow: 0 0 0 3px rgba(138, 43, 226, 0.15);
}

.sign-in-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(45deg, #ff6b6b, #ff8e53);
  color: white;
  border: none;
  border-radius: 15px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.sign-in-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.Signup-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(45deg, #9b59b6, #8e44ad);
  color: white;
  border: none;
  border-radius: 15px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 4px 15px rgba(155, 89, 182, 0.3);
}

.Signup-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(155, 89, 182, 0.4);
}

.switch-form {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.switch-form .link {
  color: #8e44ad;
  text-decoration: underline;
  cursor: pointer;
  font-weight: bold;
}

@media (max-width: 768px) {
  .form-container {
    flex-direction: column;
    align-items: center;
  }

  .form-card {
    width: 90%;
    max-width: 350px;
  }
}

@media (max-width: 480px) {
  .form-card {
    padding: 20px;
  }

  h2 {
    font-size: 20px;
  }

  .input-group input {
    padding: 10px 12px 10px 40px;
    font-size: 13px;
  }

  .sign-in-btn, .Signup-btn {
    padding: 10px;
    font-size: 14px;
  }
}
</style>