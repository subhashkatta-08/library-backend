package com.library.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.entity.BookEntity;
import com.library.exceptions.AdminNotFoundException;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.IssueRecordNotFoundException;
import com.library.exceptions.UserAlreadyExistsException;
import com.library.exceptions.UserNotFoundException;
import com.library.security.JwtService;
import com.library.service.LibraryService;
import com.nit.dto.AdminDto;
import com.nit.dto.AdminStatsDto;
import com.nit.dto.BookDto;
import com.nit.dto.IssuseRecordDto;
import com.nit.dto.LoginRequest;
import com.nit.dto.LoginResponse;
import com.nit.dto.UserDto;

@RestController
@RequestMapping("/library-api")
@CrossOrigin(origins = "*")
public class LibraryController {
	
	@Autowired
	private LibraryService service;
	
	@Autowired
	private JwtService jwt;
	
	/* USER PART */
	
	@PostMapping("/user/register")
	public ResponseEntity<?> registeruser(@RequestBody UserDto userDto){
		try {
			String msg = service.registerUser(userDto);
			return new ResponseEntity<String>(msg,HttpStatus.OK);
		}catch(UserAlreadyExistsException e) {
			return new ResponseEntity<>(e.getErrors(), HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/user/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest request){
		try {
			UserDto userDto = service.loginUser(request);
			String role = "USER";
			String token = jwt.generateToken(userDto.getEmail(), role);
			return new ResponseEntity<>(new LoginResponse(token, "Login Sucessfull", role, userDto.getName()),HttpStatus.OK);
		}catch(UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	@GetMapping("/user/actions")
	public ResponseEntity<?> getUserActions(Authentication auth){
		try {
			String email = auth.getName();
			Map<String, Integer> userActions = service.getUserActions(email);
			return ResponseEntity.ok(userActions);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GetMapping("/user/my-books")
	public ResponseEntity<?> getOwnedBooks(Authentication auth){
		try {
			String email = auth.getName();
			List<IssuseRecordDto> issusedBooks = service.getOwnedBooks(email);
			return ResponseEntity.ok(issusedBooks);
		}catch(UserNotFoundException e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/user/return/{issueId}")
	public ResponseEntity<?> returnOwnedBook(@PathVariable Integer issueId, Authentication auth) {
	    try {
	        return ResponseEntity.ok(service.returnOwnedBook(issueId, auth.getName()));

	    } catch (UserNotFoundException | IssueRecordNotFoundException e) {
	        return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);

	    } catch (RuntimeException e) {
	        return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);

	    } catch (Exception e) {
	        return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	@GetMapping("/user/details")
	public ResponseEntity<?> getUserDetails(Authentication auth){
		try {
			UserDto userDetails = service.getUserDetails(auth.getName());
			return ResponseEntity.ok(userDetails);
		}catch(UserNotFoundException e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/user/my-activity")
	public ResponseEntity<?> getUserActivity(Authentication auth){
		try {
			List<IssuseRecordDto> userActivity = service.getUserActivity(auth.getName());
			return ResponseEntity.ok(userActivity);
			
		}catch(UserNotFoundException e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/user/available-books")
	public ResponseEntity<?> getAvailableBooks(){
		try {
			List<BookDto> availableBooks = service.getAvailableBooks();
			return ResponseEntity.ok(availableBooks);			
		}catch(Exception e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/user/request/{bookId}")
	public ResponseEntity<?> userRequestBook(@PathVariable Integer bookId, Authentication auth){
		try {
			IssuseRecordDto userRequestBook = service.userRequestBook(bookId, auth.getName());
			return ResponseEntity.ok(userRequestBook);
		}catch(BookNotFoundException e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	/* ADMIN PART */
	
	@PostMapping("/admin/login")
	public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request){
		try {
			AdminDto adminDto = service.adminLogin(request);
			String role = "ADMIN";
			String token = jwt.generateToken(adminDto.getEmail(), role);
			return new ResponseEntity<>(new LoginResponse(token, "Login Sucessfull", role, adminDto.getName()),HttpStatus.OK);
		}catch(AdminNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/admin/stats")
	public ResponseEntity<?> getAdminStats(){
		try {
			AdminStatsDto adminStats = service.getAdminStats();
			return ResponseEntity.ok(adminStats);		
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
	
	@GetMapping("/admin/books")
	public ResponseEntity<?> getAllBooksForAdmin(){
		try {
			List<BookDto> allBooks = service.getAllBooks();
			return ResponseEntity.ok(allBooks);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/admin/add-book")
    public ResponseEntity<?> addBook(@RequestBody BookEntity bookEntity) {
        try {
            BookDto added = service.addBook(bookEntity);
            return ResponseEntity.ok("Book Added Sucessfully With Id No: "+added.getId());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PutMapping("/admin/edit-book/{bookId}")
    public ResponseEntity<?> editBook(@PathVariable Integer bookId, @RequestBody BookDto updatedBook) {
        try {
            BookDto edited = service.editBook(bookId, updatedBook);
            return ResponseEntity.ok(edited);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	@DeleteMapping("/admin/delete-book/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer bookId) {
        try {
            String msg = service.deleteBook(bookId);
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GetMapping("/admin/requests/pending")
	public ResponseEntity<?> getPendingRecords(){
		try {
			List<IssuseRecordDto> pendingRecords = service.getPendingRecords("PENDING");
			return ResponseEntity.ok(pendingRecords);	
		} catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PostMapping("/admin/requests/{id}/approve")
	public ResponseEntity<?> approveRequest(@PathVariable Integer id, Authentication auth){
		try {
			String msg = service.approveRequest(id, auth.getName());
			return  ResponseEntity.ok(msg);
			
		} catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PostMapping("/admin/requests/{id}/reject")
	public ResponseEntity<?> rejectRequest(@PathVariable Integer id){
		try {
			String msg = service.rejectRequest(id);
			return  ResponseEntity.ok(msg);
			
		} catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/admin/return/requested")
	public ResponseEntity<?> getReturnRequestRecords(){
		try {
			List<IssuseRecordDto> pendingRecords = service.getReturnRequestRecords("RETURN_REQUESTED");
			return ResponseEntity.ok(pendingRecords);	
		} catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
	}
	
	@PostMapping("/admin/return-request/{id}/approve")
	public ResponseEntity<?> approveReturnRequest(@PathVariable Integer id){
		try {
			String approveReturnMsg = service.approveReturnRequest(id);
			return ResponseEntity.ok(approveReturnMsg);
		}catch (IssueRecordNotFoundException e) {
			return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/admin/allusers")
	public ResponseEntity<?> getAllUsers(){
		try {
			List<UserDto> allUsers = service.getAllUsers();
			return ResponseEntity.ok(allUsers);
		} catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PostMapping("/admin/users/{id}/clear-fine")
	public ResponseEntity<?> clearFine(@PathVariable Integer id) {
	    try {
	        String msg = service.clearFine(id);
	        return ResponseEntity.ok(msg);
	    } catch (Exception e) {
	        return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
}
