import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeParseException;

class Usuario {
    private String nome;
    private String cidade;
    private String email;

    public Usuario(String nome, String cidade, String email) {
        this.nome = nome;
        this.cidade = cidade;
        this.email = email;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEmail() {
        return email;
    }
}

class Evento {
    private String nome;
    private String endereco;
    private String categoria;
    private LocalDateTime hora;
    private String descricao;

    public Evento(String nome, String endereco, String categoria, LocalDateTime hora, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.hora = hora;
        this.descricao = descricao;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDateTime getHora() {
        return hora;
    }

    public String getDescricao() {
        return descricao;
    }
}

class EventManager {
    private List<Evento> eventos;

    public EventManager() {
        this.eventos = new ArrayList<>();
    }

    public void addEvento(Evento evento) {
        eventos.add(evento);
    }

    public void removeEvento(Evento evento) {
        eventos.remove(evento);
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<Evento> getEventosFiltradoPorHora() {
        List<Evento> eventosOrdenados = new ArrayList<>(eventos);
        Collections.sort(eventosOrdenados, new Comparator<Evento>() {
            @Override
            public int compare(Evento evento1, Evento evento2) {
                return evento1.getHora().compareTo(evento2.getHora());
            }
        });
        return eventosOrdenados;
    }
}

public class Gestor {
    private static final String FILENAME = "events.data";
    private static Usuario usuario;

    public static void main(String[] args) {
        EventManager eventManager = new EventManager();

        // Carrega eventos e informações do usuário do arquivo
        loadFromFile(eventManager);

        // Se não há informações de usuário, solicita ao usuário que insira suas informações
        if (usuario == null) {
            addUserInfoToFile();
        }

        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            
            System.out.println("Seja bem-vindo, " + usuario.getNome() + "!");
            System.out.println("\n1. Ver Eventos");
            System.out.println("2. Adicionar Evento");
            System.out.println("3. Verificar informações do usuário");
            System.out.println("4. Sair");
            System.out.println("Escolha uma opção:");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha

            switch (choice) {
                case 1:
                    displayEventos(eventManager);
                    break;
                case 2:
                    addEvento(scanner, eventManager);
                    break;
                case 3:
                    displayUsuario(usuario);
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Escolha inválida. Por favor, tente novamente.");
            }
        }

        // Salva os eventos no arquivo antes de sair
        saveEventsToFile(eventManager.getEventos());
    }

    private static void loadFromFile(EventManager eventManager) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            // Lê informações do usuário
            String userInfoLine = br.readLine();
            if (userInfoLine != null) {
                String[] userInfo = userInfoLine.split(",");
                String nome = userInfo[0];
                String cidade = userInfo[1];
                String email = userInfo[2];
                usuario = new Usuario(nome, cidade, email);
            }

            // Lê eventos
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String nome = parts[0];
                String endereco = parts[1];
                String categoria = parts[2];
                LocalDateTime hora = LocalDateTime.parse(parts[3]);
                String descricao = parts[4];
                Evento evento = new Evento(nome, endereco, categoria, hora, descricao);
                eventManager.addEvento(evento);
            }
        } catch (IOException e) {
            // Erro ao ler o arquivo
            System.out.println("Erro ao carregar eventos do arquivo: " + e.getMessage());
        }
    }

    private static void displayEventos(EventManager eventManager) {
        List<Evento> eventos = eventManager.getEventos();
        if (eventos.isEmpty()) {
            System.out.println("Não há eventos disponíveis.");
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nEventos:");
            for (Evento evento : eventos) {
                System.out.println("Nome: " + evento.getNome());
                System.out.println("Endereço: " + evento.getEndereco());
                System.out.println("Categoria: " + evento.getCategoria());
                System.out.println("Hora: " + evento.getHora());
                System.out.println("Descrição: " + evento.getDescricao());
                System.out.println();
            }
            System.out.println("Ordenar a lista com base na hora dos eventos?");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    // Ordena os eventos por hora e exibe novamente
                    List<Evento> eventosOrdenados = eventManager.getEventosFiltradoPorHora();
                    displayEventosOrdenados(eventosOrdenados);
                    break;
                case 2:
                    // Continuar exibindo eventos sem ordenação
                    break;
                default:
                    System.out.println("Escolha inválida. A lista será exibida sem ordenação.");
            }
        }
    }

    private static void displayEventosOrdenados(List<Evento> eventos) {
        System.out.println("\nEventos Ordenados:");
        for (Evento evento : eventos) {
            System.out.println("Nome: " + evento.getNome());
            System.out.println("Endereço: " + evento.getEndereco());
            System.out.println("Categoria: " + evento.getCategoria());
            System.out.println("Hora: " + evento.getHora());
            System.out.println("Descrição: " + evento.getDescricao());
            System.out.println();
        }
    }

    private static void displayUsuario(Usuario usuario) {
            System.out.println("\nAqui estão as informações do usuário:");
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("Cidade: " + usuario.getCidade());
            System.out.println("email: " + usuario.getEmail());
    }

    private static void addEvento(Scanner scanner, EventManager eventManager) {
        System.out.println("Insira o nome do evento:");
        String nome = scanner.nextLine();
        System.out.println("Insira o endereço do evento:");
        String endereco = scanner.nextLine();
        System.out.println("Insira a categoria do evento:");
        String categoria = scanner.nextLine();
        LocalDateTime hora = null;
        boolean formatoValido = false;
        while (!formatoValido) {
            try {
                System.out.println("Insira a data e a hora (yyyy-MM-dd HH:mm):");
                String dateTimeString = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                hora = LocalDateTime.parse(dateTimeString, formatter);
                formatoValido = true;
            } catch (DateTimeParseException e) {
                System.out.println("Formato da data e hora inválido. Por favor, insira novamente.");
            }
        }
        System.out.println("Insira a descrição do evento:");
        String descricao = scanner.nextLine();

        // Cria o objeto Evento com os dados fornecidos
        Evento evento = new Evento(nome, endereco, categoria, hora, descricao);
        eventManager.addEvento(evento);
        System.out.println("Evento adicionado com sucesso.");
    }

    private static void saveEventsToFile(List<Evento> eventos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            // Escreve informações do usuário no arquivo
            if (usuario != null) {
                bw.write(usuario.getNome() + "," + usuario.getCidade() + "," + usuario.getEmail());
                bw.newLine();
            }

            // Escreve eventos no arquivo
            for (Evento evento : eventos) {
                bw.write(evento.getNome() + "," + evento.getEndereco() + "," + evento.getCategoria() + "," +
                        evento.getHora() + "," + evento.getDescricao());
                bw.newLine();
            }
        } catch (IOException e) {
            // Erro ao escrever no arquivo
            System.out.println("Erro ao salvar eventos no arquivo: " + e.getMessage());
        }
    }

    private static void addUserInfoToFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Por favor, insira seu nome:");
        String nome = scanner.nextLine();
        System.out.println("Por favor, insira sua cidade:");
        String cidade = scanner.nextLine();
        System.out.println("Por favor, insira seu email:");
        String email = scanner.nextLine();
        usuario = new Usuario(nome, cidade, email);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            bw.write(usuario.getNome() + "," + usuario.getCidade() + "," + usuario.getEmail());
            bw.newLine();
        } catch (IOException e) {
            // Erro ao escrever no arquivo
            System.out.println("Erro ao salvar informações do usuário no arquivo: " + e.getMessage());
        }
    }
}