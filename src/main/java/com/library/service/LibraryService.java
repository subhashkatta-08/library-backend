package com.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.library.entity.AdminEntity;
import com.library.entity.BookEntity;
import com.library.entity.IssueRecordEntity;
import com.library.entity.UserEntity;
import com.library.exceptions.AdminNotFoundException;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.FieldErrorResponse;
import com.library.exceptions.IssueRecordNotFoundException;
import com.library.exceptions.UserAlreadyExistsException;
import com.library.exceptions.UserNotFoundException;
import com.library.repository.IAdminRepository;
import com.library.repository.IBookRepository;
import com.library.repository.IIssuseRecordRepository;
import com.library.repository.IUserRepository;
import com.nit.dto.AdminDto;
import com.nit.dto.AdminStatsDto;
import com.nit.dto.BookDto;
import com.nit.dto.IssuseRecordDto;
import com.nit.dto.LoginRequest;
import com.nit.dto.UserDto;

@Service
public class LibraryService {
	
	@Autowired
	private IUserRepository userRepo;
	@Autowired
	private IAdminRepository adminRepo;
	@Autowired
	private IIssuseRecordRepository issueRecordRepo;
	@Autowired
	private IBookRepository bookRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	
	
	/* USER PART */
	
	
	public String registerUser(UserDto userDto) {
		List<FieldErrorResponse> fieldErrors = new ArrayList<FieldErrorResponse>();
		if(userRepo.findByEmail(userDto.getEmail()).isPresent()) {
			fieldErrors.add(new FieldErrorResponse("email", "Email Already Exists"));
		}
		if(userRepo.findByMobileNo(userDto.getMobileNo()).isPresent()) {
			fieldErrors.add(new FieldErrorResponse("mobileNo", "Mobile Number Already Exists"));
		}
		
		if(!fieldErrors.isEmpty()) {
			throw new UserAlreadyExistsException(fieldErrors);
		}
		
		UserEntity userEntity = new UserEntity();
		userEntity.setName(userDto.getName());
		userEntity.setEmail(userDto.getEmail());
		userEntity.setMobileNo(userDto.getMobileNo());
		userEntity.setFine(0.0);
		userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
		
		return "User Saved with Id No:: "+userRepo.save(userEntity);
	}
	
	
	public UserDto loginUser(LoginRequest request) {
		
		UserEntity userEntity;
		if(request.getIdentifier().matches("^[6-9]\\d{9}$")) {
			userEntity = userRepo.findByMobileNo(Long.parseLong(request.getIdentifier()))
							.orElseThrow(()-> new UserNotFoundException("User Doesn't Exists"));
		}else {
			userEntity = userRepo.findByEmail(request.getIdentifier())
							.orElseThrow(()-> new UserNotFoundException("User Doesn't Exists"));
		}
		if(!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
			throw new UserNotFoundException("Invalid Credentials");
		}
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}
	
	public Map<String, Integer> getUserActions(String email){
		
		UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Available"));
		
		int total = issueRecordRepo.countByUserEmail(email);
		int issused = issueRecordRepo.countByUserEmailAndStatus(email, "ISSUED");
		int pending = issueRecordRepo.countByUserEmailAndStatus(email, "PENDING");
		int returned = issueRecordRepo.countByUserEmailAndStatus(email, "RETURNED");
		
		Map<String, Integer> userActions = new HashMap<String, Integer>();
		userActions.put("TOTAL", total);
		userActions.put("ISSUED", issused);
		userActions.put("PENDING", pending);
		userActions.put("RETURNED", returned);
		
		return userActions;
	}
	
	public List<IssuseRecordDto> getOwnedBooks(String email){
		
		UserEntity userEntity = userRepo.findByEmail(email)
									.orElseThrow(() -> new UserNotFoundException("User Not Available"));
		List<IssueRecordEntity> issusedEntityRecords = issueRecordRepo.findByUserIdAndStatus(userEntity.getId(), "ISSUED");
		List<IssuseRecordDto> issusedRecords = new ArrayList<IssuseRecordDto>();
		issusedEntityRecords.forEach(entity->{
			IssuseRecordDto dto = new IssuseRecordDto();
			BeanUtils.copyProperties(entity, dto);
			dto.setBookTitle(entity.getBook().getTitle());
			issusedRecords.add(dto);
		});
		return issusedRecords;
		
	}
	
	public IssuseRecordDto returnOwnedBook(Integer issueId, String email) {

	    UserEntity user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new UserNotFoundException("User not found"));

	    IssueRecordEntity record = issueRecordRepo.findById(issueId)
	            .orElseThrow(() -> new IssueRecordNotFoundException("Issue record not found"));

	    if (!record.getUser().getId().equals(user.getId())) {
	        throw new RuntimeException("You are not allowed to request return for this record");
	    }

	    if (!"ISSUED".equalsIgnoreCase(record.getStatus())) {
	        throw new RuntimeException("Return can only be requested for issued books");
	    }

	    record.setStatus("RETURN_REQUESTED");
	    record.setReturnDate(null);

	    IssueRecordEntity savedRecord = issueRecordRepo.save(record);

	    IssuseRecordDto dto = new IssuseRecordDto();
	    BeanUtils.copyProperties(savedRecord, dto);
	    dto.setUserId(savedRecord.getUser().getId());
	    dto.setBookId(savedRecord.getBook().getId());

	    return dto;
	}

	
	public UserDto getUserDetails(String email) {
		UserEntity userEntity = userRepo.findByEmail(email)
									.orElseThrow(() -> new UserNotFoundException("User Not Available"));
		UserDto userDetails = new UserDto();
		BeanUtils.copyProperties(userEntity, userDetails);
		return userDetails;
	}
	
	public List<IssuseRecordDto> getUserActivity(String email){
		
		UserEntity userEntity = userRepo.findByEmail(email)
											.orElseThrow(() -> new UserNotFoundException("User Not Available"));
		List<IssueRecordEntity> userActivityEntity = issueRecordRepo.findByUserId(userEntity.getId());
		List<IssuseRecordDto> userActivityDto = new ArrayList<IssuseRecordDto>();
		userActivityEntity.forEach(entity ->{
			IssuseRecordDto dto = new IssuseRecordDto();
			BeanUtils.copyProperties(entity, dto);
			BookEntity book = bookRepo.findById(entity.getBook().getId()).orElse(null);
			if(book != null) {
				dto.setBookTitle(book.getTitle());
			}else {
				dto.setBookTitle("Unknown Book");
			}
			userActivityDto.add(dto);
		});
		return userActivityDto;
	}
	
	public List<BookDto> getAvailableBooks(){
		List<BookEntity> allBooks = bookRepo.findAll();
		List<BookDto> availableBooks = new ArrayList<BookDto>();
		allBooks.forEach(entity->{
			BookDto dto = new BookDto();
			BeanUtils.copyProperties(entity, dto);
			availableBooks.add(dto);
		});
		return availableBooks;
	}
	
	public IssuseRecordDto userRequestBook(Integer bookId, String email) {
		BookEntity book = bookRepo.findById(bookId)
						.orElseThrow(()->
								new BookNotFoundException("Book Not Found"));
		
		UserEntity user = userRepo.findByEmail(email)
		.orElseThrow(() ->
		        new UserNotFoundException("User not found"));
		
		boolean alreadyRequested = issueRecordRepo
									.existsByUserIdAndBookIdAndStatus(user.getId(), book.getId(), "PENDING");
		if (alreadyRequested) {
		throw new RuntimeException("You already requested this book and it is pending");
		}
		IssueRecordEntity record = new IssueRecordEntity();
		record.setUser(user);
		record.setBook(book);
		record.setIssuedBy(null);
		record.setIssueDate(null);
		record.setDueDate(null);
		record.setStatus("PENDING");
		record.setRequestDate(LocalDate.now());
		IssueRecordEntity savedRecord = issueRecordRepo.save(record);
		IssuseRecordDto requestedRecord = new IssuseRecordDto();
		BeanUtils.copyProperties(savedRecord, requestedRecord);
		requestedRecord.setUserId(savedRecord.getUser().getId());
		requestedRecord.setBookId(savedRecord.getBook().getId());
		requestedRecord.setBookTitle(savedRecord.getBook().getTitle());
		return requestedRecord;
	}
	
	
	
	/* ADMIN PART */	
	
	
	public AdminDto adminLogin(LoginRequest request) {
		AdminEntity adminEntity;
		if(request.getIdentifier().matches("^[6-9]\\d{9}$")) {
			adminEntity = adminRepo.findByMobileNo(Long.parseLong(request.getIdentifier()))
					.orElseThrow(()-> new AdminNotFoundException("Admin Doesn't Exists"));
		}else {
			adminEntity = adminRepo.findByEmail(request.getIdentifier())
					.orElseThrow(()-> new AdminNotFoundException("Admin Doesn't Exists"));
		}
		if(!passwordEncoder.matches(request.getPassword(), adminEntity.getPassword())) {
			throw new AdminNotFoundException("Invalid Credentials");
		}
		AdminDto adminDto = new AdminDto();
		BeanUtils.copyProperties(adminEntity, adminDto);
		return adminDto;
	}
	
	public AdminStatsDto getAdminStats() {

	    AdminStatsDto dto = new AdminStatsDto();

	    dto.setTotalBooks(bookRepo.count());
	    dto.setTotalIssued(issueRecordRepo.countByStatus("ISSUED"));
	    dto.setTotalPending(issueRecordRepo.countByStatus("PENDING"));
	    dto.setTotalOverdue(issueRecordRepo.countByStatusAndReturnDateIsNullAndDueDateBefore("ISSUED", LocalDate.now()));

	    return dto;
	}
	 
	public BookDto addBook(BookEntity book) {
		if(book.getAvailableCopies() == 0) {
			book.setAvailableCopies(book.getTotalCopies());
		}
		BookEntity bookEntity = bookRepo.save(book);
		BookDto bookDto = new BookDto();
		BeanUtils.copyProperties(bookEntity, bookDto);
		return bookDto;
	}
	
	public BookDto editBook(Integer bookId,BookDto updatedBook) {
		BookEntity book = bookRepo.findById(bookId).
									orElseThrow(()-> new BookNotFoundException("Book Not Found  With id: "+bookId));
		
		book.setTitle(updatedBook.getTitle());
		book.setAuthor(updatedBook.getAuthor());
		book.setCategory(updatedBook.getCategory());
		book.setTotalCopies(updatedBook.getTotalCopies());
		book.setAvailableCopies(updatedBook.getAvailableCopies());
		
		BookEntity bookEntity = bookRepo.save(book);
		BookDto bookDto = new BookDto();
		BeanUtils.copyProperties(bookEntity, bookDto);
		return bookDto;
		
	}
	
	public String deleteBook(Integer bookId) {
		BookEntity book = bookRepo.findById(bookId)
                			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));
        bookRepo.delete(book);
        return "Book deleted successfully";
	}
	
	public List<IssuseRecordDto> getPendingRecords(String status){
		List<IssueRecordEntity> recordEntityList = issueRecordRepo.findByStatus(status);
		List<IssuseRecordDto> recordDtoList = new ArrayList<>();
		
		recordEntityList.forEach(entity->{
			IssuseRecordDto dto = new IssuseRecordDto();
			BeanUtils.copyProperties(entity, dto);
			recordDtoList.add(dto);
		});
		return recordDtoList;
	}
	
	public String approveRequest(Integer id, String email) {
		IssueRecordEntity issuseRecordEntity = issueRecordRepo.findById(id)
											.orElseThrow(() -> new IssueRecordNotFoundException("Issuse Record Not Found"));
		AdminEntity adminEntity = adminRepo.findByEmail(email)
											.orElseThrow(() -> new AdminNotFoundException("Admin Not Found"));
		issuseRecordEntity.setStatus("ISSUED");
		issuseRecordEntity.getBook().setAvailableCopies(issuseRecordEntity.getBook().getAvailableCopies() - 1);
		issuseRecordEntity.setIssueDate(LocalDate.now());
		issuseRecordEntity.setDueDate(LocalDate.now().plusDays(14));
		issuseRecordEntity.setIssuedBy(adminEntity);
		issueRecordRepo.save(issuseRecordEntity);
		return "Request Issused";
	}
	
	public String rejectRequest(Integer id) {
		IssueRecordEntity issuseRecordEntity = issueRecordRepo.findById(id)
											.orElseThrow(() -> new IssueRecordNotFoundException("Issuse Record Not Found"));
		issuseRecordEntity.setStatus("REJECTED");
		issueRecordRepo.save(issuseRecordEntity);
		return "Request Rejeceted";
	}
	
	public List<IssuseRecordDto> getReturnRequestRecords(String status){
		List<IssueRecordEntity> recordEntityList = issueRecordRepo.findByStatus(status);
		List<IssuseRecordDto> recordDtoList = new ArrayList<>();
		
		recordEntityList.forEach(entity->{
			IssuseRecordDto dto = new IssuseRecordDto();
			BeanUtils.copyProperties(entity, dto);
			recordDtoList.add(dto);
		});
		return recordDtoList;
	}
	
	public String approveReturnRequest(Integer id) {
		IssueRecordEntity issueRecordEntity = issueRecordRepo.findById(id)
											.orElseThrow(() -> new IssueRecordNotFoundException("Issuse Record Not Found"));
		LocalDate today = LocalDate.now();
		
		if(issueRecordEntity.getStatus().equals("RETURN_REQUESTED")) {
			issueRecordEntity.setStatus("RETURNED");
			issueRecordEntity.setReturnDate(today);
			
			LocalDate dueDate = issueRecordEntity.getDueDate();
			
			if (dueDate != null && today.isAfter(dueDate)) {
	            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
	            double finePerDay = 5.0; 
	            double fine = daysLate * finePerDay;

	            issueRecordEntity.getUser().setFine(fine);
	        } else {
	            issueRecordEntity.getUser().setFine(0.0);
	        }
		}
		issueRecordRepo.save(issueRecordEntity).getId();
		return "Book Returned Sucessfuly ";
	}
	
	public List<UserDto> getAllUsers(){
		List<UserEntity> allUsers = userRepo.findAll();
		List<UserDto> all = new ArrayList<UserDto>();
		
		allUsers.forEach(userEntity ->{
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			all.add(userDto);
		});
		
		return all;
	}
	
	public String clearFine(Integer id) {
	    UserEntity user = userRepo.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    user.setFine(0.0);
	    userRepo.save(user);

	    return "Fine cleared successfully";
	}

	
	
	
	
	/* BOOK PART */
	public List<BookDto> getAllBooks(){
		List<BookEntity> allEntityBooks = bookRepo.findAll();
		List<BookDto> allDtoBooks = new ArrayList<>();
		
		allEntityBooks.forEach(entity ->{
			BookDto dto = new BookDto();
			BeanUtils.copyProperties(entity, dto);
			allDtoBooks.add(dto);
		});
		
		return allDtoBooks;
	}

	
	
	

}
