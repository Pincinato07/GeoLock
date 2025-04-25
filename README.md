# 📱 GeoLockApp – Android GPS Boundary Monitor

**GeoLockApp** é um aplicativo Android que permite definir uma barreira virtual com base em duas coordenadas GPS e monitora, em tempo real, se o dispositivo ultrapassa essa linha. Se ultrapassar, o app dispara um alarme visual e sonoro, registrando o evento em um histórico de bloqueios.

---

## 🚀 Funcionalidades

- 📍 Calibração com dois pontos (Ponto A e Ponto B)
- 🧭 Criação de uma linha virtual invisível com base nas coordenadas
- 🛑 Alerta instantâneo com som e tela de bloqueio se o usuário ultrapassar a linha
- 📊 Histórico de bloqueios (com data e hora)
- 🔄 Monitoramento contínuo em background com Foreground Service
- 🧠 Botão de controle manual (ativar/desativar o monitoramento)
- ⚙️ Tela de configurações

---

## 📸 Tela Principal

- **Pegar localização atual**
- **Calibrar limite** → direciona para a tela de calibração (2 pontos)
- **Ver histórico de bloqueios**
- **Configurações** → ativa/desativa o serviço de monitoramento

---

## 🔧 Estrutura de Pastas

```
├── MainActivity.java
├── AlertaActivity.java
├── CalibrarActivity.java
├── ConfirmacaoActivity.java
├── ConfiguracoesActivity.java
├── HistoricoActivity.java
├── GeoLockService.java
├── /res/layout/
│   ├── activity_main.xml
│   ├── activity_alerta.xml
│   ├── activity_calibrar.xml
│   ├── activity_configuracoes.xml
│   ├── activity_confirmacao.xml
│   ├── activity_historico.xml
├── AndroidManifest.xml
```

---

## 📲 Permissões Requeridas

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## ⚙️ Como rodar o projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/GeoLockApp.git
   ```

2. Abra no Android Studio

3. Conecte um dispositivo físico ou emulador

4. Rode o app (▶️)

---

## 📌 Observações técnicas

- O app utiliza `FusedLocationProviderClient` com alta precisão
- Foreground Service obrigatório em Android 10+ com `foregroundServiceType="location"`
- Persistência com `SharedPreferences` para armazenar coordenadas e histórico
- Funciona com a tela desligada ou app fechado, desde que o serviço esteja ativado

---

## 📅 Futuras melhorias

- Integração com Google Maps para visualização dos pontos
- Exportação de histórico para `.csv`
- Agendamento automático ao ligar o celular (BootReceiver)
- Detecção mais precisa com geofencing circular (opcional)

---

## 📜 Licença

Este projeto é de uso livre para fins educacionais ou pessoais. Comercialização não autorizada sem permissão.

```
