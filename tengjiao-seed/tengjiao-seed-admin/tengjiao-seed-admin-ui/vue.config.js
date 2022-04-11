'use strict'
// vue.config.js
process.env.VUE_APP_VERSION = require('./package.json').version

const defaultSettings = require('./src/settings.js')
const name = defaultSettings.title || 'Vue Element Admin' // page title

const path = require('path')
function resolve (dir) {
  return path.join(__dirname, dir)
}

// 优先级顺序 env.development 配置 port， npm run dev --port = XXX ，最后 9527
const port = process.env.port || process.env.npm_config_port || 9527 // dev port

module.exports = {
  // cli3.0以上使用publicPath
  // 该应用(打包后)的将部署到的基础路径
  // 默认为'/'，如果要部署在网站二级目录下如https://foo.github.io/bar/, 用 '/bar/'
  // html 页面中可以这样访问 <%=BASE_URL%>
  publicPath: process.env.VUE_APP_PUBLIC_PATH || '/',
  outputDir: 'dist',
  assetsDir: 'static',
  // eslint
  lintOnSave: process.env.NODE_ENV === 'development',
  productionSourceMap: false,

  configureWebpack: config => {
    // 应用程序名称
    config.name = name // html 页面中可以这样访问 <%= webpackConfig.name %>，而 <%= htmlWebpackPlugin.options.title %> 默认(除非覆写)取得的是 package.json#title的值
    // 配置路径别名
    Object.assign(config.resolve, {
      alias: {
        '@': resolve('src')
      }
    })
    // 生产环境下删除浏览器控制台日志
    if (process.env.NODE_ENV === 'production') {
      // 配置删除 console.log
      config.optimization.minimizer[0].options.terserOptions.compress.drop_console = true
    }
  },

  // webpack-dev-server 相关配置
  devServer: {
    publicPath: '/',
    open: process.platform === 'vue',
    host: '127.0.0.1',
    // 设置开发环境 node 端口号
    port: port,
    https: false,
    hotOnly: false,
    overlay: {
      warnings: false,
      errors: true
    },
    // 加载 mock-server 测试数据接口
    before: app => { if (process.env.NODE_ENV === 'development') { require('./mock/mock-server.js')(app) } },
    // http 代理
    /*
    proxy: {
      'api':{                            // 1、'api' 本身代表http//${host}:${port}/api
        target: 'http://127.0.0.1:8080', // 2、将前端请求 http://${host}:${port}/api/xxx 改写为 '${target}/api/xxx'
        pathRewrite: {
          '^/api':''                     // 3、pathRewrite: {'^/api' : ''} 又把 '${target}/api/xxx' 的api替换成'' 改写为 ${target}/xxx (node请求后端接口)
        },
        changeOrigin: true               // node请求后端接口发送请求头的 host 设置成 ${target}，后端根据host匹配到对应的网站
      }
    },
    */
    disableHostCheck: true
  },

  chainWebpack (config) {
    config.plugins.delete('preload') // TODO: need test
    config.plugins.delete('prefetch') // TODO: need test

    // 解决 svg 图标在vue不显示的问题 需要先安装 npm install svg-sprite-loader -D
    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
    // 保留空格 set preserveWhitespace
    config.module
      .rule('vue')
      .use('vue-loader')
      .loader('vue-loader')
      .tap(options => {
        options.compilerOptions.preserveWhitespace = true
        return options
      })
      .end()

    // 优化生产打包
    config
      .when(process.env.NODE_ENV !== 'development',
        config => {
          config
            .plugin('ScriptExtHtmlWebpackPlugin')
            .after('html')
            .use('script-ext-html-webpack-plugin', [{
              // `runtime` must same as runtimeChunk name. default is `runtime`
              inline: /runtime\..*\.js$/
            }])
            .end()
          config
            .optimization.splitChunks({
            chunks: 'all',
            cacheGroups: {
              libs: {
                name: 'chunk-libs',
                test: /[\\/]node_modules[\\/]/,
                priority: 10,
                chunks: 'initial' // only package third parties that are initially dependent
              },
              elementUI: {
                name: 'chunk-elementUI', // split elementUI into a single package
                priority: 20, // the weight needs to be larger than libs and app or it will be packaged into libs or app
                test: /[\\/]node_modules[\\/]_?element-ui(.*)/ // in order to adapt to cnpm
              },
              commons: {
                name: 'chunk-commons',
                test: resolve('src/components'), // can customize your rules
                minChunks: 3, //  minimum common number
                priority: 5,
                reuseExistingChunk: true
              }
            }
          })
          config.optimization.runtimeChunk('single')
        }
      )
  }
}
