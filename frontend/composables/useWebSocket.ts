import { Client } from '@stomp/stompjs'

export function useSeatWebSocket(tripId: string, onSeatUpdate: (message: any) => void) {
  const config = useRuntimeConfig()
  let stompClient: Client | null = null
  const isConnected = ref(false)

  const connect = () => {
    if (process.server) return

    const wsUrl = config.public.wsUrl || 'ws://localhost:8080/ws'
    
    stompClient = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 3000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        // console.log('[STOMP]:', str)
      },
      onConnect: () => {
        isConnected.value = true
        console.log(`[WebSocket] Đã kết nối STOMP thành công tới chuyến: ${tripId}`)
        
        stompClient?.subscribe(`/topic/trips/${tripId}/seats`, (msg) => {
          try {
            const data = JSON.parse(msg.body)
            onSeatUpdate(data)
          } catch (e) {
            console.error('Lỗi parse WebSocket payload:', e)
          }
        })
      },
      onDisconnect: () => {
        isConnected.value = false
        console.log('[WebSocket] Đã ngắt kết nối STOMP')
      },
      onStompError: (frame) => {
        console.error('[WebSocket] STOMP Error:', frame.headers['message'], frame.body)
      }
    })

    stompClient.activate()
  }

  const disconnect = () => {
    if (stompClient) {
      stompClient.deactivate()
      stompClient = null
      isConnected.value = false
    }
  }

  onMounted(() => {
    if (tripId) {
      connect()
    }
  })

  onUnmounted(() => {
    disconnect()
  })

  return {
    isConnected,
    connect,
    disconnect
  }
}
