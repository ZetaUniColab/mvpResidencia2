package com.easy.chatbot.repository;

import com.easy.chatbot.entitys.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositório para acesso a dados da entidade Cliente.
 * Estende JpaRepository para herdar operações CRUD padrão e suporte a paginação.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Consulta derivada (Derived Query Method) para localizar um cliente pelo número de WhatsApp.
     * O Spring Data JPA gera a query SQL automaticamente baseada na assinatura do método.
     *
     * @param whatsapp Número de telefone do cliente.
     * @return Optional contendo o cliente, se encontrado.
     */
    Optional<Cliente> findByWhatsapp(String whatsapp);
}