// 合同数据文件 - 前端使用
// 当后端不可用时，使用此文件作为备用数据

import { contracts as defaultContracts } from './defaultContracts'

// 获取所有合同列表
export const getAvailableContracts = () => {
  const src = getContractsSource()
  return Object.values(src).map(contract => ({
    id: contract.id,
    title: contract.title,
    description: contract.description,
    totalErrors: contract.totalErrors
  }))
}

// 根据ID获取合同
export const getContractById = (id) => {
  const src = getContractsSource()
  return src[id] || null
}

// 获取合同标题
export const getContractTitle = (id) => {
  const src = getContractsSource()
  const contract = src[id]
  return contract ? contract.title : `合同${id}`
}

// 获取头部信息索引（不可选择）
export const getHeaderIndices = (id) => {
  const src = getContractsSource()
  const contract = src[id]
  return contract && Array.isArray(contract.headerIndices) ? contract.headerIndices : []
}

// 获取错误索引
export const getErrorIndices = (id) => {
  const src = getContractsSource()
  const contract = src[id]
  return contract ? contract.errorIndices : [6, 10, 14, 18, 20]
}

// 获取合同内容
export const getContractContent = (id) => {
  const src = getContractsSource()
  const contract = src[id]
  return contract ? contract.content : []
}

// 获取错误说明
export const getErrorExplanations = (id) => {
  const src = getContractsSource()
  const contract = src[id]
  return contract ? contract.errorExplanations : {}
}

// 基于需求：
// 1) 过滤掉不可选择的头部信息（headerIndices）
// 2) 返回用于游戏的可选择条款列表
export const getPlayableContent = (id) => {
  const src = getContractsSource()
  const contract = src[id]
  if (!contract) return []
  const headerSet = new Set(contract.headerIndices || [])
  const playable = (contract.content || []).map((t, i) => ({ t, i })).filter(row => !headerSet.has(row.i))
  // 若后续需要“约20条”的严格数量控制，可在此处做 slice 或补齐
  return playable.map(row => row.t)
}

// 将错误索引映射到可选择条款的新索引空间
export const getPlayableErrorIndices = (id) => {
  const src = getContractsSource()
  const contract = src[id]
  if (!contract) return []
  const headerSet = new Set(contract.headerIndices || [])
  // 原 -> 新 的索引映射
  let newIndex = -1
  const mapOldToNew = new Map()
  ;(contract.content || []).forEach((_, oldIdx) => {
    if (!headerSet.has(oldIdx)) {
      newIndex += 1
      mapOldToNew.set(oldIdx, newIndex)
    }
  })
  return (contract.errorIndices || [])
    .map(oldIdx => mapOldToNew.has(oldIdx) ? mapOldToNew.get(oldIdx) : null)
    .filter(v => v !== null)
}

// ========== 动态加载支持（通过新增/删除 src/data/contracts/*.json 动态调整） ==========
let _dynamicContracts = null

function loadDynamicContracts() {
  try {
    // webpack 环境下使用 require.context 动态聚合 json 文件
    const ctx = require.context('./contracts', false, /\.json$/)
    const loaded = {}
    ctx.keys().forEach(key => {
      const data = ctx(key)
      const c = data && data.default ? data.default : data
      if (c && (c.id != null)) {
        loaded[c.id] = c
      }
    })
    _dynamicContracts = Object.keys(loaded).length > 0 ? loaded : null
  } catch (e) {
    _dynamicContracts = null
  }
}

function getContractsSource() {
  if (_dynamicContracts === null) loadDynamicContracts()
  return _dynamicContracts || defaultContracts
}

// 可选导出：供外部手动触发重载（例如热更新场景）
export const reloadContracts = () => { _dynamicContracts = null; loadDynamicContracts() }
