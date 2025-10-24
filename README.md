# 🤖 Codifica con Guali

Aplicación web educativa para enseñar conceptos básicos de programación a través de un robot programable.

## 📋 Requisitos del Sistema

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose (para despliegue con contenedores)
- MariaDB 11.2 (si no usas Docker)

## 🚀 Despliegue Local con Docker (Recomendado)

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd codifica-guali
```

### 2. Construir y ejecutar con Docker Compose

```bash
docker-compose up --build
```

La aplicación estará disponible en: `http://localhost:8080`

### 3. Detener la aplicación

```bash
docker-compose down
```

### 4. Detener y eliminar volúmenes (reset completo)

```bash
docker-compose down -v
```

## 🔧 Despliegue Local sin Docker

### 1. Configurar MariaDB

Crear base de datos:

```sql
CREATE DATABASE codifica_guali;
CREATE USER 'guali_user'@'localhost' IDENTIFIED BY 'guali_password';
GRANT ALL PRIVILEGES ON codifica_guali.* TO 'guali_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configurar application.properties

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/codifica_guali
spring.datasource.username=guali_user
spring.datasource.password=guali_password
```

### 3. Compilar y ejecutar

```bash
mvn clean package
java -jar target/codifica-guali-1.0.0.jar
```

## 👤 Credenciales por Defecto

- **Usuario:** admin
- **Contraseña:** admin123

⚠️ **IMPORTANTE:** Cambia estas credenciales después del primer inicio de sesión.

## 🌐 Opciones de Publicación en Internet

### Opción 1: Railway (Recomendado - Gratis)

Railway ofrece despliegue gratuito con SSL automático.

#### Pasos:

1. Crea cuenta en [Railway.app](https://railway.app)
2. Instala Railway CLI:
   ```bash
   npm i -g @railway/cli
   ```
3. Inicia sesión:
   ```bash
   railway login
   ```
4. Inicializa el proyecto:
   ```bash
   railway init
   ```
5. Agrega la base de datos MariaDB:
   ```bash
   railway add
   # Selecciona MariaDB
   ```
6. Configura variables de entorno en Railway Dashboard:
   - `DB_HOST`: (se genera automáticamente)
   - `DB_PORT`: 3306
   - `DB_NAME`: codifica_guali
   - `DB_USER`: (se genera automáticamente)
   - `DB_PASSWORD`: (se genera automáticamente)

7. Despliega:
   ```bash
   railway up
   ```

**URL final:** `https://tu-proyecto.railway.app`

**Costo:** Gratis para 500 horas/mes

---

### Opción 2: Render.com (Gratis)

Render ofrece hosting gratuito con SSL automático.

#### Pasos:

1. Crea cuenta en [Render.com](https://render.com)
2. Crea un nuevo "Web Service"
3. Conecta tu repositorio de GitHub
4. Configura:
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -jar target/codifica-guali-1.0.0.jar`
   - **Environment:** Java 17
5. Agrega una base de datos PostgreSQL (gratis) o MariaDB
6. Configura las variables de entorno
7. Deploy

**URL final:** `https://tu-proyecto.onrender.com`

**Costo:** Gratis (con limitaciones: app se apaga después de 15 min de inactividad)

---

### Opción 3: Heroku

#### Pasos:

1. Instala Heroku CLI
2. Crea `Procfile` en la raíz:
   ```
   web: java -jar target/codifica-guali-1.0.0.jar
   ```
3. Crea app:
   ```bash
   heroku create tu-app-name
   ```
4. Agrega JawsDB MySQL (alternativa a MariaDB):
   ```bash
   heroku addons:create jawsdb:kitefin
   ```
5. Configura variables:
   ```bash
   heroku config:set DB_HOST=<host>
   heroku config:set DB_PORT=3306
   heroku config:set DB_NAME=<database>
   heroku config:set DB_USER=<user>
   heroku config:set DB_PASSWORD=<password>
   ```
6. Deploy:
   ```bash
   git push heroku main
   ```

**Costo:** Desde $7/mes (ya no tiene plan gratuito)

---

### Opción 4: AWS EC2 (Producción)

Para ambientes de producción con más control.

#### Pasos básicos:

1. Crea una instancia EC2 (Ubuntu 22.04)
2. Instala Docker y Docker Compose
3. Clona el repositorio
4. Configura el Security Group para permitir puerto 80/443
5. Ejecuta:
   ```bash
   docker-compose up -d
   ```
6. Configura un dominio y Nginx como reverse proxy
7. Instala SSL con Let's Encrypt

**Costo:** Desde $5/mes (t2.micro)

---

### Opción 5: DigitalOcean App Platform

#### Pasos:

1. Crea cuenta en DigitalOcean
2. Crea una nueva App
3. Conecta tu repositorio
4. Agrega un componente de base de datos (Managed Database - MariaDB)
5. Configura variables de entorno
6. Deploy automático

**Costo:** Desde $5/mes

---

### Opción 6: Google Cloud Run (Serverless)

Para escalado automático y pago por uso.

#### Pasos:

1. Crea proyecto en Google Cloud
2. Habilita Cloud Run API
3. Construye imagen Docker:
   ```bash
   gcloud builds submit --tag gcr.io/PROJECT-ID/codifica-guali
   ```
4. Despliega:
   ```bash
   gcloud run deploy --image gcr.io/PROJECT-ID/codifica-guali --platform managed
   ```
5. Conecta a Cloud SQL (MariaDB)

**Costo:** Pago por uso (muy económico para tráfico bajo)

---

## 📊 Comparación de Opciones

| Plataforma | Costo Inicial | SSL Gratis | Facilidad | BD Incluida | Recomendado Para |
|------------|---------------|------------|-----------|-------------|------------------|
| Railway | Gratis* | ✅ | ⭐⭐⭐⭐⭐ | ✅ | Estudiantes/Proyectos académicos |
| Render | Gratis* | ✅ | ⭐⭐⭐⭐⭐ | ✅ | Demo/Prototipos |
| Heroku | $7/mes | ✅ | ⭐⭐⭐⭐ | ✅ (addon) | Desarrollo |
| AWS EC2 | $5/mes | ❌** | ⭐⭐⭐ | ❌*** | Producción |
| DigitalOcean | $5/mes | ✅ | ⭐⭐⭐⭐ | ✅ (addon) | Producción |
| Google Cloud Run | Variable | ✅ | ⭐⭐⭐ | ❌*** | Escalable |

\* Con limitaciones  
\** Requiere configuración manual  
\*** Requiere servicio adicional

---

## 🏗️ Estructura del Proyecto

```
codifica-guali/
├── src/
│   ├── main/
│   │   ├── java/com/umg/codificaguali/
│   │   │   ├── config/          # Configuración y seguridad
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositorios
│   │   │   ├── service/         # Lógica de negocio
│   │   │   └── CodificaGualiApplication.java
│   │   └── resources/
│   │       ├── static/          # Frontend (HTML, CSS, JS)
│   │       │   ├── admin/       # Páginas de administración
│   │       │   ├── css/
│   │       │   └── js/
│   │       └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 🎮 Uso de la Aplicación

### Para Usuarios Finales

1. Accede a `http://localhost:8080` (o tu URL pública)
2. Se carga automáticamente una pista aleatoria
3. Programa movimientos usando los botones:
   - ⬆️ Adelante
   - ⬅️ Izquierda
   - ➡️ Derecha
   - 🔁 Bucle (repite comandos)
4. Presiona "▶️ Ejecutar" para ver al robot en acción
5. 🔄 Reiniciar para intentar de nuevo

### Para Administradores

1. Accede a `http://localhost:8080/login.html`
2. Inicia sesión con credenciales de administrador
3. Gestiona:
   - **Pistas:** Crear, editar, eliminar pistas
   - **Usuarios:** Gestionar administradores
   - **Estadísticas:** Ver métricas de uso
   - **Auditoría:** Revisar acciones del sistema

## 🔒 Seguridad

- Autenticación con Spring Security
- Contraseñas encriptadas con BCrypt
- Protección CSRF deshabilitada para API REST
- Registro de auditoría de todas las acciones administrativas

## 📈 Características Implementadas

✅ Tablero de juego con pistas personalizables  
✅ Sistema de comandos (adelante, izquierda, derecha, bucle)  
✅ Validación de movimientos en tiempo real  
✅ Panel de administración completo  
✅ Gestión de pistas (CRUD)  
✅ Sistema de autenticación  
✅ Registro de auditoría  
✅ Estadísticas de uso  
✅ Exportar/Importar pistas  
✅ Diseño responsive  
✅ Interfaz moderna y diferente al documento original  

## 🛠️ Tecnologías Utilizadas

- **Backend:**
  - Java 17
  - Spring Boot 3.2.0
  - Spring Security
  - Spring Data JPA
  - MariaDB

- **Frontend:**
  - HTML5
  - CSS3 (con gradientes y animaciones modernas)
  - JavaScript Vanilla (sin frameworks)

- **DevOps:**
  - Docker & Docker Compose
  - Maven

## 📝 Notas Importantes

1. La aplicación crea automáticamente:
   - Usuario administrador por defecto
   - 3 pistas de ejemplo

2. Para producción, se recomienda:
   - Cambiar credenciales por defecto
   - Habilitar HTTPS
   - Configurar backup de base de datos
   - Implementar logging centralizado
   - Configurar firewall

3. La interfaz es completamente diferente al documento original:
   - Diseño moderno con gradientes
   - Colores personalizados
   - Animaciones suaves
   - Responsive design

## 🐛 Troubleshooting

### Error de conexión a la base de datos

```bash
# Verifica que MariaDB esté corriendo
docker-compose ps

# Revisa los logs
docker-compose logs mariadb
```

### La aplicación no inicia

```bash
# Revisa los logs de la aplicación
docker-compose logs app

# Verifica que el puerto 8080 esté disponible
lsof -i :8080
```

### No puedo iniciar sesión

- Verifica que estés usando las credenciales correctas
- Revisa que la base de datos tenga datos iniciales
- Limpia cookies del navegador

## 📧 Soporte

Para problemas o preguntas sobre el proyecto, contacta al equipo de desarrollo.

## 📄 Licencia

Este proyecto es para uso educativo en la Universidad Mariano Gálvez de Guatemala.