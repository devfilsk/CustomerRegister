FROM node:18-alpine

WORKDIR /app

# Install Angular CLI globally
RUN npm install -g @angular/cli

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Expose port 4200
EXPOSE 4200

# Start the application
CMD ["ng", "serve", "--host", "0.0.0.0", "--poll", "2000"] 