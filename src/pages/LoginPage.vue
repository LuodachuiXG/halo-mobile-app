<template>
  <div class="container">
    <div class="title text-weight-bold text-italic">
      <text>Kalo</text>
    </div>

    <transition
      appear
      enter-active-class="animated fadeInUp slow"
      leave-active-class="animated fadeOutDown slow"
    >
      <div class="input-div q-pa-lg">
        <q-input
          class="q-mb-md"
          :standout="isDark ? 'bg-white text-black' : 'bg-primary text-white'"
          v-model="url"
          label="Halo 站点地址"
          hint="需要带上 http / https"
        />
        <q-input
          class="q-mb-lg"
          :standout="isDark ? 'bg-white text-black' : 'bg-primary text-white'"
          v-model="username"
          label="用户名"
        />
        <q-input
          v-model="password"
          :standout="isDark ? 'bg-white text-black' : 'bg-primary text-white'"
          label="密码"
          type="password"
        />
      </div>
    </transition>
    <transition
      appear
      enter-active-class="animated fadeInRight slow delay-1s"
      leave-active-class="animated fadeOutRight slow"
    >
      <div class="q-px-lg q-pt-sm" style="text-align: right">
        <q-btn-group flat>
          <q-btn
            flat
            :color="isDark ? 'white' : 'primary'"
            :loading="isLogging"
            @click="onLogin"
            label="登录"
            icon="login"
          />
          <q-btn
            flat
            color="negative"
            @click="onExit"
            label="退出"
            icon="logout"
          />
        </q-btn-group>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { App } from '@capacitor/app'
import { useQuasar } from 'quasar'
// import { generateToken } from 'src/js/common'
import axios from 'axios'
const $q = useQuasar()

// 标记当前是否是暗色模式
const isDark = ref($q.dark.isActive)
watch(
  () => $q.dark.isActive,
  (val) => {
    isDark.value = val
  }
)

// Halo 站点地址
const url = ref('https://halo.loac.cc')
// 用户名
const username = ref('admin')
// 密码
const password = ref('123456')
// 是否正在登录中
const isLogging = ref(false)

/**
 * 登录按钮点击事件
 */
const onLogin = () => {
  // isLogging.value = true
  if (
    url.value.length === 0 ||
    username.value.length === 0 ||
    password.value.length === 0
  ) {
    $q.notify({
      type: 'negative',
      message: '请将内容填写完整'
    })
    isLogging.value = false
    return
  }

  axios.defaults.withCredentials = false
  const instance = axios.create({
    baseURL: 'https://halo.loac.cc/',
    timeout: 2000,
    withCredentials: false
  })

  instance.get('/login/public-key', {
    headers: {
      'Access-Control-Allow-Origin': url.value
    }
  }).then(res => {
    alert('success')
  }).catch(err => {
    alert('fail:' + err)
  })

  // 首先获取 PublicKey
  // axios({
  //   method: 'get',
  //   url: url.value + '/login/public-key',
  //   headers: {
  //     Host: 'halo.loac.cc',
  //     Connection: 'keep-alive',
  //     'sec-ch-ua': 'Not/A)Brand";v="99", "Google Chrome";v="115", "Chromium";v="115',
  //     Accept: 'application/json, text/plain, */*',
  //     'X-XSRF-TOKEN': 'c8953613-4fd9-4447-bbe9-fe091a698643',
  //     'X-Requested-With': 'XMLHttpRequest',
  //     'sec-ch-ua-mobile': '?0',
  //     'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36',
  //     'sec-ch-ua-platform': 'Windows',
  //     'Sec-Fetch-Site': 'same-origin',
  //     'Sec-Fetch-Mode': 'cors',
  //     'Sec-Fetch-Dest': 'empty',
  //     Referer: 'https://halo.loac.cc/console/login',
  //     'Accept-Encoding': 'gzip, deflate, br',
  //     'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7',
  //     Cookie: 'waf_sc=5889647726; SESSION=03f87073-7306-4c10-a08b-07b682dbf969; XSRF-TOKEN=c8953613-4fd9-4447-bbe9-fe091a698643'
  //   }
  // })
  //   .then(function (res) {
  //     console.log(res)
  //     $q.dialog({
  //       title: 'success',
  //       message: res
  //     })
  //   })
  //   .catch(function (err) {
  //     console.log(err)
  //     $q.dialog({
  //       title: 'failed',
  //       message: err.errMsg
  //     })
  //   })
}

/**
 * 退出按钮点击事件
 */
const onExit = () => {
  if ($q.platform.is.android) {
    App.exitApp()
  }
}
</script>

<style lang="scss">
@import '../css/quasar.variables';

.title {
  font-family: 'ali', serif;
  text-align: center;
  margin-top: 7vh;
  font-size: 7rem;
}

.body--light {
  .title {
    color: $primary;
  }
}

.body--dark {
  .title {
    color: white;
  }
}
</style>
