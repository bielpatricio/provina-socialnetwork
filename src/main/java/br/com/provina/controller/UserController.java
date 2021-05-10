package br.com.provina.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.provina.controller.dto.ItemDto;
import br.com.provina.controller.dto.UserDetailDto;
import br.com.provina.controller.dto.UserDto;
import br.com.provina.controller.form.UserForm;
import br.com.provina.model.User;
import br.com.provina.repository.ItemRepository;
import br.com.provina.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

	private UserRepository userRepository;
	private ItemRepository itemRepository;

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
	}

	@PostMapping
	@Transactional
	public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserForm form, UriComponentsBuilder uriBuilder) {
		User user = form.convert();
		userRepository.save(user);

		URI uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
		return ResponseEntity.created(uri).body(new UserDto(user));

	}

	@GetMapping("/{id}/items")
	public ResponseEntity<List<ItemDto>> listUserItems(@PathVariable("id") Long id) {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			List<ItemDto> items = ItemDto.convertList(user.get().getItem());
			return ResponseEntity.ok().body(items);
		}

		return ResponseEntity.notFound().build();

	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDetailDto> detail(@PathVariable Long id) {

		Optional<User> optional = userRepository.findById(id);
		if (optional.isPresent()) {
			return ResponseEntity.ok(new UserDetailDto(optional.get()));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Optional<User> optional = userRepository.findById(id);
		if (optional.isPresent()) {
			userRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();

	}
}
