<template>
  <div class="container">

    <q-dialog v-model="showDialog" persistent transition-show="scale" transition-hide="scale">
      <q-card class="bg-primary text-white" style="width: 300px">
        <q-card-section>
          <div class="text-h6">温馨提示</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          这是一个测试
        </q-card-section>

        <q-card-actions align="right" class="bg-white text-primary">
          <q-btn flat label="好的" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>
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
      <q-card class="q-ma-lg q-pa-xs">
        <q-card-section>
          <div class="input-div">
            <q-input class="q-mb-md" outlined v-model="url" label="Halo 站点地址" hint="需要带上 http / https"/>
            <q-input class="q-mb-lg" outlined v-model="username" label="用户名"/>
            <q-input v-model="password" outlined label="密码" type="password"/>
          </div>
        </q-card-section>
        <q-card-actions>
          <q-btn flat class="text-primary" @click="onLogin">登录</q-btn>
          <q-btn flat @click="onExit">退出</q-btn>
        </q-card-actions>
      </q-card>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { App } from '@capacitor/app'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const url = ref('')
const username = ref('')
const password = ref('')
const showDialog = ref(false)

const onLogin = () => {
  showDialog.value = true
}

const onExit = () => {
  if ($q.platform.is.android) {
    alert(1)
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
