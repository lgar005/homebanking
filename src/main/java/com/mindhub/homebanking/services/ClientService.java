package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<ClientDTO> getClientsDTO();
    ClientDTO getClientDTO(Long id);
    ClientDTO getClientDTO(Authentication authentication);
    Client getAuthenticatedClient (Authentication authentication);
    Client findById(Long id);
    Client findByEmail(String email);
    void saveClient(Client client);
}
