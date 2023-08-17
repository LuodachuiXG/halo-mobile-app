import axios from 'axios'
import { getUrl, getAccessToken } from 'src/js/common'

function service (options = {}) {
  options.url = getUrl() + options.url
  options.header = {
    'Content-Type': 'application/json',
    'ADMIN-Authorization': getAccessToken()
  }
  options.dataType = 'json'
  return new Promise((_resolve, _reject) => {
    options.success = (res) => {
      if (res.statusCode === 200) {
        // 如果 allData 设为 true，则返回网络请求的所有数据
        if (options.allData !== undefined && options.allData) {
          _resolve(res)
        } else {
          _resolve(res.data.data)
        }
      } else {
        _reject(res.data.message)
      }
    }
    options.fail = (err) => {
      _reject(err.errMsg)
    }
    axios.request(options)
  })
}
export default service
