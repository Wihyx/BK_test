import { ref } from 'vue'

// 全局唯一 Audio 实例，组件卸载后不销毁
const audio = new Audio()
audio.volume = 0.3

// 播放器响应式状态
const playlist = ref([])         // 播放列表
const currentIdx = ref(-1)       // 当前播放索引
const currentTrack = ref(null)   // 当前曲目信息
const isPlaying = ref(false)     // 是否正在播放
const progress = ref(0)          // 播放进度 0~1
const currentTime = ref('00:00') // 当前时间（格式化）
const duration = ref('00:00')    // 总时长（格式化）
const coverAngle = ref(0)        // 唱片旋转角度
const subtitleOn = ref(false)    // 歌词开关
const lyricsLines = ref([])      // 歌词行数组 [{time, text}]
const lyricIdx = ref(0)          // 当前歌词行索引
const showList = ref(false)      // 是否显示播放列表弹窗

let angleTimer = null            // 旋转定时器 ID

// 秒数格式化为 mm:ss
function fmt(t) {
  if (!t || isNaN(t)) return '00:00'
  const m = Math.floor(t / 60), s = Math.floor(t % 60)
  return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
}

// 绑定音频事件：进度更新、歌词同步、播放结束自动切歌
function bindEvents() {
  audio.ontimeupdate = () => {
    if (audio.duration) {
      progress.value = audio.currentTime / audio.duration
      currentTime.value = fmt(audio.currentTime)
      // 歌词同步：找到当前时间对应的最后一句歌词
      if (lyricsLines.value.length) {
        let idx = 0
        for (let i = 0; i < lyricsLines.value.length; i++) {
          if (lyricsLines.value[i].time <= audio.currentTime) idx = i
        }
        lyricIdx.value = idx
      }
    }
  }
  audio.onloadedmetadata = () => { duration.value = fmt(audio.duration) }
  audio.onended = () => { next() }     // 播放完毕自动下一首
}

bindEvents()

// 选择并播放第 i 首
function select(i) {
  if (i < 0 || i >= playlist.value.length) return
  currentIdx.value = i
  currentTrack.value = playlist.value[i]
  audio.src = currentTrack.value.path
  audio.play().then(() => { isPlaying.value = true; showList.value = false; startSpin() }).catch(() => {})
}

// 播放/暂停切换
function toggle() {
  if (!playlist.value.length || currentIdx.value < 0) return
  if (!audio.src) { select(currentIdx.value); return }
  isPlaying.value = !isPlaying.value
  isPlaying.value ? audio.play().then(() => startSpin()).catch(() => {}) : (audio.pause(), stopSpin())
}

// 下一首 / 上一首（循环）
function next() { playlist.value.length && select((currentIdx.value + 1) % playlist.value.length) }
function prev() { playlist.value.length && select((currentIdx.value - 1 + playlist.value.length) % playlist.value.length) }

// 拖动跳转到 val(0~1) 位置
function seek(val) { audio.currentTime = val * (audio.duration || 1) }

// 开始 / 停止唱片旋转动画
function startSpin() { stopSpin(); angleTimer = setInterval(() => { coverAngle.value = (coverAngle.value + 0.5) % 360 }, 50) }
function stopSpin() { angleTimer && clearInterval(angleTimer) }

// 导出 composable，组件内调用 useMusic() 获取播放器状态与方法
export function useMusic() {
  return { audio, playlist, currentIdx, currentTrack, isPlaying, progress, currentTime, duration, coverAngle, subtitleOn, lyricsLines, lyricIdx, showList, select, toggle, next, prev, seek, startSpin, stopSpin }
}
