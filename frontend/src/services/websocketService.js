import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.connected = false;
        this.subscriptions = new Map();
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.connectionCallback = null;
    }

    connect() {
        return new Promise((resolve, reject) => {
            try {
                console.log('WebSocket bağlantısı başlatılıyor...');
                
                const socket = new SockJS('http:
                    transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
                    timeout: 5000
                });
                console.log('SockJS bağlantısı oluşturuldu');
                
                this.stompClient = Stomp.over(() => socket);
                
                socket.onopen = () => {
                    console.log('SockJS bağlantısı açıldı');
                };
                
                socket.onclose = (event) => {
                    console.log('SockJS bağlantısı kapandı:', event);
                    this.connected = false;
                    if (this.connectionCallback) {
                        this.connectionCallback(false);
                    }
                };
                
                socket.onerror = (error) => {
                    console.error('SockJS hatası:', error);
                    this.connected = false;
                    if (this.connectionCallback) {
                        this.connectionCallback(false);
                    }
                };
                
                this.stompClient.debug = (str) => {
                    console.log('Stomp Debug:', str);
                };
                
                this.stompClient.connect(
                    {},
                    (frame) => {
                        console.log('WebSocket bağlantısı başarılı:', frame);
                        this.connected = true;
                        this.reconnectAttempts = 0;
                        if (this.connectionCallback) {
                            this.connectionCallback(true);
                        }
                        resolve();
                    },
                    (error) => {
                        console.error('WebSocket bağlantı hatası:', error);
                        this.connected = false;
                        
                        if (this.reconnectAttempts < this.maxReconnectAttempts) {
                            this.reconnectAttempts++;
                            console.log(`Yeniden bağlanma denemesi ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);
                            setTimeout(() => {
                                this.connect().then(resolve).catch(reject);
                            }, 2000);
                        } else {
                            reject(error);
                        }
                    }
                );
            } catch (error) {
                console.error('WebSocket başlatma hatası:', error);
                reject(error);
            }
        });
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.connected = false;
            this.subscriptions.clear();
            this.reconnectAttempts = 0;
        }
    }

    subscribe(topic, callback) {
        if (!this.connected || !this.stompClient) {
            console.error('WebSocket bağlı değil!');
            return null;
        }

        try {
            const subscription = this.stompClient.subscribe(topic, (message) => {
                try {
                    const data = JSON.parse(message.body);
                    callback(data);
                } catch (error) {
                    console.error('Mesaj parse hatası:', error);
                }
            });

            this.subscriptions.set(topic, subscription);
            return subscription;
        } catch (error) {
            console.error('Subscribe hatası:', error);
            return null;
        }
    }

    unsubscribe(topic) {
        const subscription = this.subscriptions.get(topic);
        if (subscription) {
            try {
                subscription.unsubscribe();
                this.subscriptions.delete(topic);
            } catch (error) {
                console.error('Unsubscribe hatası:', error);
            }
        }
    }

    send(destination, message) {
        if (!this.connected || !this.stompClient) {
            console.error('WebSocket bağlı değil!');
            return;
        }

        try {
            this.stompClient.send(destination, {}, JSON.stringify(message));
        } catch (error) {
            console.error('Mesaj gönderme hatası:', error);
        }
    }

    isConnected() {
        return this.connected && this.stompClient;
    }

    onConnectionChange(callback) {
        if (this.stompClient) {
            this.connectionCallback = callback;
        }
    }
}

const websocketService = new WebSocketService();
export default websocketService; 