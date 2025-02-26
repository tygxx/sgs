<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'SgsDoc',
  data() {
    return {
      url: 'http://doc.sgs.vip/sgs-vue'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>