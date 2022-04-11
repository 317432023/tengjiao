<template>
    <div>
        <template slot="prepend"><i :class="value"></i></template>
        <el-popover v-model="visible" placement="bottom" width="300" trigger="focus" slot="append">
            <el-row class="popover">
                <el-col v-for="(item,index) in icons" :key=index :span="6">
                    <el-button style="font-size:20px;color: #009688" :icon="item" @click="selectIcon(item)"></el-button>
                </el-col>
            </el-row>
            <el-input size="mini" placeholder="选择图标" :value="value" slot="reference" :prefix-icon="value"></el-input>
        </el-popover>
    </div>
</template>

<script>
import iconList from './icon'

export default {
  data () {
    return {
      icons: iconList,
      value: '',
      visible: false
    }
  },
  methods: {
    selectIcon (iconCls) {
      this.visible = false
      this.value = iconCls
      this.$emit('input', iconCls)
    }
  }
}
</script>

<style scoped>
    .popover {
        max-height: 300px;
        overflow: auto;
    }
</style>
