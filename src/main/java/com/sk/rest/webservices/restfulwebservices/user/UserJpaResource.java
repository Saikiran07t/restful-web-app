package com.sk.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sk.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.sk.rest.webservices.restfulwebservices.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {

	
	private UserRepository repository;
	
	private PostRepository postRepository; 

	public UserJpaResource( UserRepository repository,PostRepository postRepository) {
		this.repository = repository;
		this.postRepository= postRepository;
	}

	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers() {
		return repository.findAll();
	}

	// apart from giving o/p we can give them additional links to navigate
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id) {
		Optional<User> user=repository.findById(id);
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		EntityModel<User> entityModel= EntityModel.of(user.get()); // we are wrapping user class to entity model
		//this helps to create a link pointing to Controller method (hereit is retrieveAllUsers)
		WebMvcLinkBuilder link =linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		return entityModel;
	}

	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User savedUser = repository.save(user);
		// when we want the created user details to be seen by client we can append to
		// current request
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")// to path of current request we
																						// are appending a variable
				.buildAndExpand(savedUser.getId()) // and replace {} with buildAndExpand
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
	  repository.deleteById(id);
	}
	
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrievePostsForUser(@PathVariable int id) {
		Optional<User> user=repository.findById(id);
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		return user.get().getPosts();
	}
	
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post) {
		Optional<User> user = repository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		post.setUser(user.get());
		
		Post savedPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId())
				.toUri();   

		return ResponseEntity.created(location).build();

	}
	
	@GetMapping("/jpa/users/{id}/posts/{postId}")
	public Post retrievePostForUserByPostId(@PathVariable int id,@PathVariable int postId) {		
		Optional<User> user=repository.findById(id);
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		Optional<Post> post = user.get().getPosts().stream().filter(pos -> pos.getId().equals(postId)).findFirst();
		if(post.isEmpty())
			throw new PostNotFoundException("id:"+postId);
		return post.get();
	}

}
