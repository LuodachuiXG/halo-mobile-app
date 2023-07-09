<template>
  <div class="container">
    <transition
      appear
      enter-active-class="animated fadeInDown slow"
      leave-active-class="animated fadeOutUp slow">
      <div class="title text-weight-bold text-italic">
        <text>Kalo</text>
      </div>
    </transition>

    <transition
      appear
      enter-active-class="animated fadeInUp slow"
      leave-active-class="animated fadeOutDown slow">
      <div class="input-div q-pa-lg">
        <q-input class="q-mb-md" standout="bg-primary text-white" v-model="url" label="Halo 站点地址" hint="需要带上 http / https"/>
        <q-input class="q-mb-lg" standout="bg-primary text-white" v-model="username" label="用户名"/>
        <q-input v-model="password" standout="bg-primary text-white" label="密码" type="password"/>
      </div>
    </transition>

    <transition
      appear
      enter-active-class="animated fadeInRight slow delay-1s"
      leave-active-class="animated fadeOutRight slow">
      <div class="q-px-lg q-pt-sm" style="text-align: right">
        <q-btn-group flat>
          <q-btn flat color="primary" :loading="isLogging" @click="onLogin" label="登录" icon="login" />
          <q-btn flat color="negative" @click="onExit" label="退出" icon="logout"/>
        </q-btn-group>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { App } from '@capacitor/app'
import { useQuasar } from 'quasar'
const $q = useQuasar()

// Halo 站点地址
const url = ref('')
// 用户名
const username = ref('')
// 密码
const password = ref('')
// 是否正在登录中
const isLogging = ref(false)

const onLogin = () => {
  isLogging.value = true
  $q.dialog({
    title: '温馨提示',
    message: '这是一个测试',
    persistent: true,
    ok: {
      color: 'primary',
      flat: true
    }
  }).onOk(() => {
    isLogging.value = false
  })
}

const onExit = () => {
  if ($q.platform.is.android) {
    App.exitApp()
  }
}
</script>

<style lang="scss">
@import "../css/quasar.variables";

.title {
  font-family: 'ali', serif;
  color: $primary;
  text-align: center;
  margin-top: 7vh;
  font-size: 7rem;
}
</style>
