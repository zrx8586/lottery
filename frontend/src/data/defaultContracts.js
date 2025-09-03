// 默认内置合同集合（作为动态目录为空或加载失败时的回退）
import c1 from './contracts/1-labor.json'
import c2 from './contracts/2-lease.json'
import c3 from './contracts/3-purchase.json'
import c4 from './contracts/4-tech.json'
import c5 from './contracts/5-service.json'

export const contracts = {
  [c1.id]: c1,
  [c2.id]: c2,
  [c3.id]: c3,
  [c4.id]: c4,
  [c5.id]: c5
}


