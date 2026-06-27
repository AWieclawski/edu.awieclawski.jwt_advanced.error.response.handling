package edu.awieclawski.app.facade;

import edu.awieclawski.app.dao.UserRepository;
import edu.awieclawski.app.dto.ErrorResponseDto;
import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.exception.EntityNotFoundException;
import edu.awieclawski.app.exception.DeleteEntityException;
import edu.awieclawski.app.exception.SaveEntityException;
import edu.awieclawski.app.exception.UpdateEntityException;
import edu.awieclawski.app.jwt.IJwtPublicAuthenticationService;
import edu.awieclawski.app.mapper.UserMapper;
import edu.awieclawski.app.model.UserEntity;
import edu.awieclawski.app.service.ErrorMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component(ResourceFacade.BEAN_NAME)
@RequiredArgsConstructor
public class ResourceFacade {

    public static final String BEAN_NAME = "edu.awieclawski.app.facade.ResourceFacade";

    private final IJwtPublicAuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final ErrorMessageService errorMessageService;

    public UserResponseDTO getAuthenticatedUser() {
        Authentication authentication = authenticationService.getAuthentication();
        doThrowRandomExceptionOrNot(authentication.getName());
        UserEntity entity = userRepository.findByLogin(authentication.getName());
        if (entity != null) {
            return UserMapper.toDto(entity);
        }
        throw new EntityNotFoundException("User: " + authentication.getName() + " not found!" );
    }

    public List<ErrorResponseDto> getErrors() {
        return errorMessageService.getAllDtoErrors();
    }

    private void doThrowRandomExceptionOrNot(String name) {
        List<Class<? extends RuntimeException>> classList =
                Arrays.asList(SaveEntityException.class, DeleteEntityException.class, UpdateEntityException.class);
        int nextedInt = new Random().nextInt(classList.size() * 2);
        switch (nextedInt) {
            case 1:
                throw new SaveEntityException("Save failed for User: " + name);
            case 2:
                throw new DeleteEntityException("Delete failed for User: " + name);
            case 3:
                throw new UpdateEntityException("Update failed for User: " + name);
        }
    }

}
