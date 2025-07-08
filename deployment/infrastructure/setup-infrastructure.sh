#!/bin/bash

# Infrastructure Setup Script for Ticket Management System
# This script sets up and manages the infrastructure services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_header "Checking Prerequisites"

    # Check if Docker is running
    if ! docker info &> /dev/null; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi

    # Check if docker-compose is available
    if ! command -v docker-compose &> /dev/null; then
        print_error "docker-compose is not installed. Please install docker-compose first."
        exit 1
    fi

    # Check available ports
    local ports=(5432 5050 6379 2181 9092 29092 9411)
    for port in "${ports[@]}"; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_warning "Port $port is already in use. Please stop the service using this port."
        fi
    done

    print_status "Prerequisites check completed"
}

# Start infrastructure services
start_infrastructure() {
    print_header "Starting Infrastructure Services"

    print_status "Starting PostgreSQL, Redis, Kafka, and Zipkin..."
    docker-compose up -d

    print_status "Waiting for services to be ready..."
    wait_for_services

    print_status "Infrastructure services started successfully!"
}

# Wait for services to be ready
wait_for_services() {
    print_status "Waiting for PostgreSQL..."
    timeout 60 bash -c 'until docker-compose exec -T postgres pg_isready -U ticketuser -d ticketdb; do sleep 2; done' || {
        print_error "PostgreSQL failed to start within 60 seconds"
        exit 1
    }

    print_status "Waiting for Redis..."
    timeout 30 bash -c 'until docker-compose exec -T redis redis-cli ping; do sleep 2; done' || {
        print_error "Redis failed to start within 30 seconds"
        exit 1
    }

    print_status "Waiting for Zookeeper..."
    timeout 60 bash -c 'until docker-compose exec -T zookeeper echo ruok | nc localhost 2181 | grep imok; do sleep 5; done' || {
        print_error "Zookeeper failed to start within 60 seconds"
        exit 1
    }

    print_status "Waiting for Kafka..."
    timeout 60 bash -c 'until docker-compose exec -T kafka kafka-topics --bootstrap-server localhost:9092 --list; do sleep 5; done' || {
        print_error "Kafka failed to start within 60 seconds"
        exit 1
    }

    print_status "Waiting for Zipkin..."
    timeout 30 bash -c 'until curl -f http://localhost:9411/health; do sleep 2; done' || {
        print_error "Zipkin failed to start within 30 seconds"
        exit 1
    }

    print_status "All infrastructure services are ready!"
}

# Check service health
check_health() {
    print_header "Checking Service Health"

    local services=("postgres" "redis" "zookeeper" "kafka" "zipkin")

    for service in "${services[@]}"; do
        if docker-compose ps $service | grep -q "Up"; then
            print_status "✅ $service is running"
        else
            print_error "❌ $service is not running"
        fi
    done
}

# Show service information
show_service_info() {
    print_header "Service Information"

    echo "📊 Service URLs and Ports:"
    echo "  • PostgreSQL: localhost:5432 (Database: ticketdb, User: ticketuser)"
    echo "  • pgAdmin: http://localhost:5050 (Email: admin@admin.com, Password: admin)"
    echo "  • Redis: localhost:6379"
    echo "  • Zookeeper: localhost:2181"
    echo "  • Kafka: localhost:9092 (Internal), localhost:29092 (External)"
    echo "  • Zipkin: http://localhost:9411"
    echo ""

    echo "🔧 Useful Commands:"
    echo "  • View logs: docker-compose logs -f"
    echo "  • Stop services: docker-compose down"
    echo "  • Restart services: docker-compose restart"
    echo "  • Check status: docker-compose ps"
    echo ""

    echo "📝 Database Connection:"
    echo "  • Host: localhost"
    echo "  • Port: 5432"
    echo "  • Database: ticketdb"
    echo "  • Username: ticketuser"
    echo "  • Password: ticketpass"
    echo ""

    echo "🔍 Health Check URLs:"
    echo "  • Zipkin: http://localhost:9411/health"
    echo "  • Redis: docker-compose exec redis redis-cli ping"
    echo "  • PostgreSQL: docker-compose exec postgres pg_isready -U ticketuser"
    echo "  • Kafka: docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list"
}

# Create Kafka topics
create_kafka_topics() {
    print_header "Creating Kafka Topics"

    local topics=(
        "user.events"
        "ticket.events"
        "order.events"
        "payment.events"
        "notification.events"
        "event.events"
    )

    for topic in "${topics[@]}"; do
        print_status "Creating topic: $topic"
        docker-compose exec kafka kafka-topics \
            --create \
            --bootstrap-server localhost:9092 \
            --replication-factor 1 \
            --partitions 3 \
            --topic "$topic" || {
            print_warning "Topic $topic might already exist"
        }
    done

    print_status "Listing all topics:"
    docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
}

# Initialize database
initialize_database() {
    print_header "Initializing Database"

    # Check if initialization script exists
    if [ -f "../api-gateway/scripts/init-database.sh" ]; then
        print_status "Running database initialization script..."
        docker-compose exec postgres bash -c "chmod +x /docker-entrypoint-initdb.d/init-database.sh && /docker-entrypoint-initdb.d/init-database.sh"
    else
        print_warning "Database initialization script not found. Skipping..."
    fi
}

# Stop infrastructure services
stop_infrastructure() {
    print_header "Stopping Infrastructure Services"

    print_status "Stopping all services..."
    docker-compose down

    print_status "Infrastructure services stopped"
}

# Clean up (remove volumes)
cleanup() {
    print_header "Cleaning Up Infrastructure"

    print_warning "This will remove all data volumes. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_status "Removing volumes..."
        docker-compose down -v
        print_status "Cleanup completed"
    else
        print_status "Cleanup cancelled"
    fi
}

# Show logs
show_logs() {
    local service=$1
    if [ -z "$service" ]; then
        print_header "Showing All Logs"
        docker-compose logs -f
    else
        print_header "Showing Logs for $service"
        docker-compose logs -f "$service"
    fi
}

# Main function
main() {
    case "${1:-start}" in
        "start")
            check_prerequisites
            start_infrastructure
            create_kafka_topics
            initialize_database
            check_health
            show_service_info
            ;;
        "stop")
            stop_infrastructure
            ;;
        "restart")
            stop_infrastructure
            sleep 2
            start_infrastructure
            check_health
            ;;
        "status")
            check_health
            show_service_info
            ;;
        "logs")
            show_logs "$2"
            ;;
        "cleanup")
            cleanup
            ;;
        "topics")
            create_kafka_topics
            ;;
        "init-db")
            initialize_database
            ;;
        *)
            echo "Usage: $0 {start|stop|restart|status|logs|cleanup|topics|init-db}"
            echo ""
            echo "Commands:"
            echo "  start     - Start infrastructure services"
            echo "  stop      - Stop infrastructure services"
            echo "  restart   - Restart infrastructure services"
            echo "  status    - Check service health and show info"
            echo "  logs      - Show logs (all or specific service)"
            echo "  cleanup   - Remove all data volumes"
            echo "  topics    - Create Kafka topics"
            echo "  init-db   - Initialize database"
            exit 1
            ;;
    esac
}

# Run main function
main "$@" 