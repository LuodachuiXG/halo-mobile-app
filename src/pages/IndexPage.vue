<template>
  <q-page class="flex flex-center">
    <div>
      <q-input standout="bg-teal text-white" v-model="text" label="Custom standout"/>
      <q-btn color="deep-orange" glossy label="你好啊" @click="btnClick"/>
    </div>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'

const text = ref('')
const $q = useQuasar()

const btnClick = () => {
  $q.dialog({
    title: 'Hello',
    message: '你输入了' + text.value
  }).onOk(() => {
    console.log(text.value)
  })
}
</script>
