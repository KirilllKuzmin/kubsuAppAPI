package com.kubsu.userService.controller;

import com.kubsu.userService.controller.dto.JwtResponseDTO;
import com.kubsu.userService.controller.dto.SignInRequestDTO;
import com.kubsu.userService.controller.dto.SignUpRequestDTO;
import com.kubsu.userService.model.*;
import com.kubsu.userService.repository.*;
import com.kubsu.userService.service.UserDetailsImpl;
import com.kubsu.userService.util.JwtUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final DegreeOfStudyRepository degreeOfStudyRepository;

    private final FacultyRepository facultyRepository;

    private final SpecialtyRepository specialtyRepository;

    private final GroupRepository groupRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          DegreeOfStudyRepository degreeOfStudyRepository,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository,
                          FacultyRepository facultyRepository,
                          SpecialtyRepository specialtyRepository,
                          GroupRepository groupRepository,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.degreeOfStudyRepository = degreeOfStudyRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.facultyRepository = facultyRepository;
        this.specialtyRepository = specialtyRepository;
        this.groupRepository = groupRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequestDTO signInRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDTO.getUsername(), signInRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponseDTO res = new JwtResponseDTO();
        res.setToken(jwt);
        res.setId(userDetails.getId());
        res.setUsername(userDetails.getUsername());
        res.setRoles(roles);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequestDTO signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("username is already taken");
        }
        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        Set<Role> roles = new HashSet<>();
        Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_STUDENT);

        if (userRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("role not found");
        }

        roles.add(userRole.get());

        Map<EParseKubsuData, String> parseDataKubsu = loginKubSUStudent(signUpRequest.getUsername(), signUpRequest.getPassword());

        Optional<DegreeOfStudy> degreeOfStudy = degreeOfStudyRepository.findById(1L);

        Faculty faculty = new Faculty(parseDataKubsu.get(EParseKubsuData.FACULTY_NAME));
        facultyRepository.save(faculty);

        Specialty specialty = new Specialty(parseDataKubsu.get(EParseKubsuData.SPECIALTY_NAME), faculty, degreeOfStudy.get());
        specialtyRepository.save(specialty);

        Group group = new Group(parseDataKubsu.get(EParseKubsuData.GROUP_NAME), specialty);
        groupRepository.save(group);

        User user = new User();
        user.setKubsuUserId(Long.valueOf(parseDataKubsu.get(EParseKubsuData.KUBSU_USER_ID)));
        user.setUsername(signUpRequest.getUsername());
        user.setFullName(parseDataKubsu.get(EParseKubsuData.FULL_NAME));
        user.setEmail(parseDataKubsu.get(EParseKubsuData.EMAIL));
        user.setPassword(hashedPassword);
        user.setGroup(group);
        user.setStartEducationDate(null);
        user.setEndEducationDate(null);
        user.setCreationDate(OffsetDateTime.now());
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok("User registered success");
    }

    private Map<EParseKubsuData, String> loginKubSUStudent(String user, String password) {
        Map<EParseKubsuData, String> studentData = new HashMap<>();
        try {
            Connection.Response response = Jsoup.connect("https://kubsu.ru/user/")
                    .data("name", user,
                            "pass", password,
                            "form_id", "user_login")
                    .method(Connection.Method.POST)
                    .timeout(10000).execute();

            Document documentPortfolio = Jsoup.connect("https://www.kubsu.ru/public-portfolio").cookies(response.cookies()).get();

            String [] fio = documentPortfolio.select("head > title").text().split(Pattern.quote("|"));
            if (fio[0].trim().equals("Гость"))
                throw new IllegalArgumentException("Неверный логин или пароль");
            studentData.put(EParseKubsuData.FULL_NAME, fio[0].trim());

            Element groupElement = documentPortfolio.selectFirst("head meta[about*=\"/ru/taxonomy/term/\"]");
            String group = Objects.requireNonNull(groupElement).attr("content");
            studentData.put(EParseKubsuData.GROUP_NAME, group);

            Element directionElement = documentPortfolio
                    .selectFirst("div.ds-1col.entity.entity-student-edu.student-edu-student-edu.view-mode-teaser.clearfix");
            Element subDirectionElement = Objects.requireNonNull(directionElement).selectFirst("h2");
            String facultyAndDirection = Objects.requireNonNull(subDirectionElement).text();

            String [] splitFacultyAndDirection = facultyAndDirection.split(Pattern.quote(" - "));

            studentData.put(EParseKubsuData.FACULTY_NAME, splitFacultyAndDirection[0].trim());
            studentData.put(EParseKubsuData.SPECIALTY_NAME, splitFacultyAndDirection[1].trim());

            Document documentUser = Jsoup.connect("https://www.kubsu.ru/user").cookies(response.cookies()).get();

            Element kubsuIdElement = documentUser.selectFirst("head meta[about*=\"/ru/user/\"]");
            String kubsuId = Objects.requireNonNull(kubsuIdElement).attr("resource");
            kubsuId = kubsuId.substring(kubsuId.lastIndexOf("/") + 1);
            studentData.put(EParseKubsuData.KUBSU_USER_ID, kubsuId);

            Document documentUserEdit = Jsoup.connect("https://www.kubsu.ru/user/" + kubsuId + "/edit").cookies(response.cookies()).get();
            Element emailElement = documentUserEdit.selectFirst("input#edit-mail");
            studentData.put(EParseKubsuData.EMAIL, emailElement.attr("value"));

        } catch (IOException e) {

            e.printStackTrace();
        }

        return studentData;
    }
}
