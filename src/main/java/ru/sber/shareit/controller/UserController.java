package ru.sber.shareit.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.service.UserService;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RequestMapping("/users")
@Controller
public class UserController {
	private final UserService userService;

	@GetMapping("/{userId}")
	public String getUserById(@PathVariable long userId) {
		userService.getUsersById(userId);
		return "users/getUser";
	}

	@GetMapping
	public List<UserInfoDto> getUsers() {
		return userService.getUsers();
	}

	@PostMapping
	public UserDto addUser(@Valid @RequestBody UserDto userDto) {
		return userService.create(userDto);
	}

	@PatchMapping("/{userId}")
	public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
		return userService.update(userId, userDto);
	}

	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable long userId) {
		userService.delete(userId);
	}

//	@GetMapping
//	public String getEmployees(Model model) {
//		model.addAttribute("employees", service.getEmployees());
//		return "employees";
//	}
//
//	@GetMapping("/add/{supervisorId}")
//	public String addEmployee(@PathVariable int supervisorId, Model model) {
//		EmployeeEntity employee = new EmployeeEntity();
//		model.addAttribute("employee", employee);
//		return "add-employee";
//	}
//
//	@PostMapping("/createEmployee/{supervisorId}")
//	public String saveEmployee(@Valid @ModelAttribute("employee") EmployeeEntity employee,
//	                           BindingResult bindingResult,
//	                           @PathVariable int supervisorId) {
//		if (bindingResult.hasErrors()) {
//			return "add-employee";
//		}
//		service.create(employee, supervisorId);
//		return "redirect:/supervisor/" + supervisorId;
//	}
//
//
//	@GetMapping("/employees/{supervisorId}")
//	public String getEmployeesBySupervisorId(@PathVariable int supervisorId, Model model) {
//		Collection<EmployeeEntity> employees = service.getEmployeesBySupervisorId(supervisorId);
//		model.addAttribute("employees", employees);
//		return "employees-supervisorId";
//	}
//
//	@GetMapping("/{id}")
//	public String getEmployeeById(@PathVariable int id, Model model) {
//		EmployeeEntity employee = service.getEmployeeById(id);
//		model.addAttribute("employee", employee);
//		return "employees-id";
//	}
//
//	@GetMapping("/update/{id}")
//	public String updateEmployee(@PathVariable int id, Model model) {
//		EmployeeEntity employee = service.getEmployeeById(id);
//		model.addAttribute("employee", employee);
//		return "update-employee";
//	}
//
//	@PostMapping("/updateEmployee/{id}")
//	public String updateEmployeeById(@Valid @ModelAttribute("employee") EmployeeEntity employee,
//	                                 BindingResult bindingResult,
//	                                 @PathVariable int id) {
//		if (bindingResult.hasErrors()) {
//			return "update-employee";
//		}
//		employee.setId(id);
//		service.update(employee);
//		return "redirect:/employee";
//	}
//
//	@GetMapping("/delete/{id}")
//	public String deleteEmployee(@PathVariable int id, Model model) {
//		EmployeeEntity employee = service.delete(id);
//		model.addAttribute("employee", employee);
//		return "redirect:/supervisor";
//	}
}
