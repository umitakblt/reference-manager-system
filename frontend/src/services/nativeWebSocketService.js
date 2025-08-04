class NativeWebSocketService {
    constructor() {
        this.ws = null;
        this.connected = false;
        this.isConnecting = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectInterval = 2000;
        this.connectionCallback = null;
        this.messageCallbacks = new Map();
    }

    // WebSocket bağlantısını başlat
    connect() {
        return new Promise((resolve, reject) => {
            try {
                console.log('Native WebSocket bağlantısı başlatılıyor...');
                this.isConnecting = true;
                
                const wsUrl = 'ws://localhost:8083/websocket';
                this.ws = new WebSocket(wsUrl);
                
                this.ws.onopen = (event) => {
                    console.log('Native WebSocket bağlantısı açıldı:', event);
                    this.connected = true;
                    this.isConnecting = false;
                    this.reconnectAttempts = 0;
                    
                    if (this.connectionCallback) {
                        this.connectionCallback(true);
                    }
                    
                    resolve();
                };
                
                this.ws.onclose = (event) => {
                    console.log('Native WebSocket bağlantısı kapandı:', event);
                    this.connected = false;
                    this.isConnecting = false;
                    
                    if (this.connectionCallback) {
                        this.connectionCallback(false);
                    }
                    
                    // Yeniden bağlanma denemesi
                    if (this.reconnectAttempts < this.maxReconnectAttempts) {
                        this.reconnectAttempts++;
                        console.log(`Yeniden bağlanma denemesi ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);
                        setTimeout(() => {
                            this.connect().catch(console.error);
                        }, this.reconnectInterval);
                    }
                };
                
                this.ws.onerror = (error) => {
                    console.error('Native WebSocket hatası:', error);
                    this.connected = false;
                    this.isConnecting = false;
                    
                    if (this.connectionCallback) {
                        this.connectionCallback(false);
                    }
                    
                    reject(error);
                };
                
                this.ws.onmessage = (event) => {
                    try {
                        const message = JSON.parse(event.data);
                        console.log('Native WebSocket mesajı alındı:', message);
                        
                        // Mesaj tipine göre callback'leri çağır
                        if (this.messageCallbacks.has(message.type)) {
                            this.messageCallbacks.get(message.type).forEach(callback => {
                                callback(message);
                            });
                        }
                        
                        // Genel mesaj callback'i
                        if (this.messageCallbacks.has('*')) {
                            this.messageCallbacks.get('*').forEach(callback => {
                                callback(message);
                            });
                        }
                    } catch (error) {
                        console.error('Mesaj parse hatası:', error);
                    }
                };
                
            } catch (error) {
                console.error('Native WebSocket başlatma hatası:', error);
                reject(error);
            }
        });
    }

    // Bağlantıyı kes
    disconnect() {
        if (this.ws) {
            this.ws.close();
            this.connected = false;
            this.messageCallbacks.clear();
            this.reconnectAttempts = 0;
        }
    }

    // Mesaj gönder
    send(type, data) {
        if (!this.connected || !this.ws) {
            console.error('WebSocket bağlı değil!');
            return;
        }

        try {
            const message = {
                type: type,
                data: data,
                timestamp: new Date().toISOString()
            };
            
            this.ws.send(JSON.stringify(message));
            console.log('Mesaj gönderildi:', message);
        } catch (error) {
            console.error('Mesaj gönderme hatası:', error);
        }
    }

    // Mesaj dinleyicisi ekle
    onMessage(type, callback) {
        if (!this.messageCallbacks.has(type)) {
            this.messageCallbacks.set(type, []);
        }
        
        // Duplicate callback kontrolü
        const callbacks = this.messageCallbacks.get(type);
        if (!callbacks.includes(callback)) {
            callbacks.push(callback);
        }
    }

    // Mesaj dinleyicisini kaldır
    offMessage(type, callback) {
        if (this.messageCallbacks.has(type)) {
            const callbacks = this.messageCallbacks.get(type);
            const index = callbacks.indexOf(callback);
            if (index > -1) {
                callbacks.splice(index, 1);
            }
        }
    }

    // Bağlantı durumunu kontrol et
    isConnected() {
        return this.connected && this.ws && this.ws.readyState === WebSocket.OPEN;
    }

    // Bağlantı durumunu dinle
    onConnectionChange(callback) {
        this.connectionCallback = callback;
    }

    // Ping gönder
    ping() {
        this.send('PING', 'ping');
    }

    // Flight update gönder
    sendFlightUpdate(action, flight) {
        const flightUpdate = {
            action: action,
            flight: flight,
            timestamp: new Date().toISOString()
        };
        this.send('FLIGHT_UPDATE', flightUpdate);
    }

    // İstatistikleri al
    getStats() {
        return {
            connected: this.connected,
            isConnecting: this.isConnecting,
            reconnectAttempts: this.reconnectAttempts,
            maxReconnectAttempts: this.maxReconnectAttempts,
            messageCallbacksCount: this.messageCallbacks.size,
            readyState: this.ws ? this.ws.readyState : null
        };
    }
}

// Singleton instance
const nativeWebSocketService = new NativeWebSocketService();
export default nativeWebSocketService; 