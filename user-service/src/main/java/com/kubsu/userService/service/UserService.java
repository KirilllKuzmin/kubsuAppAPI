package com.kubsu.userService.service;

import com.kubsu.userService.exception.DegreeOfStudyNotFoundException;
import com.kubsu.userService.exception.GroupNotFoundException;
import com.kubsu.userService.exception.SpecialtyNotFoundException;
import com.kubsu.userService.exception.UserNotFoundException;
import com.kubsu.userService.model.*;
import com.kubsu.userService.repository.*;
import com.kubsu.userService.util.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final DegreeOfStudyRepository degreeOfStudyRepository;

    private final FacultyRepository facultyRepository;

    private final SpecialtyRepository specialtyRepository;

    private final GroupRepository groupRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Unable to find user with id: " + id));
    }

    public List<String> authorization(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> tokenAndIdAndRoles = new ArrayList<>();

        tokenAndIdAndRoles.add(jwtToken);

        tokenAndIdAndRoles.add(String.valueOf(userDetails.getId()));

        tokenAndIdAndRoles.addAll(userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );

        return tokenAndIdAndRoles;
    }

    public void registration(String username, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_STUDENT).orElseThrow(() ->
                        new UserNotFoundException("Unable to find user role: " + ERole.ROLE_STUDENT));

        roles.add(userRole);

        Map<EParseKubsuData, String> parseDataKubsu = authAndParseKubsuStudent(username, password);

        //Пока заглушка, что всенаправления являются бакалавриатом
        DegreeOfStudy degreeOfStudy = degreeOfStudyRepository.findById(1L).orElseThrow(() ->
                new DegreeOfStudyNotFoundException("unable to find degree of study id: 1L"));

        if (!facultyRepository.existsByName(parseDataKubsu.get(EParseKubsuData.FACULTY_NAME)))
            facultyRepository.save(new Faculty(parseDataKubsu.get(EParseKubsuData.FACULTY_NAME)));

        Faculty faculty = facultyRepository.findByName(parseDataKubsu.get(EParseKubsuData.FACULTY_NAME)).orElseThrow();

        if (!specialtyRepository.existsByNameAndFacultyAndDegreeOfStudy(
                parseDataKubsu.get(EParseKubsuData.SPECIALTY_NAME), faculty, degreeOfStudy))
            specialtyRepository.save(
                    new Specialty(parseDataKubsu.get(EParseKubsuData.SPECIALTY_NAME), faculty, degreeOfStudy));

        Specialty specialty = specialtyRepository
                .findByNameAndFacultyAndDegreeOfStudy(
                        parseDataKubsu.get(EParseKubsuData.SPECIALTY_NAME), faculty, degreeOfStudy)
                .orElseThrow(() ->
                        new SpecialtyNotFoundException("Unable to find specialty " + EParseKubsuData.SPECIALTY_NAME));

        if (!groupRepository.existsByNameAndSpecialty(parseDataKubsu.get(EParseKubsuData.GROUP_NAME), specialty))
            groupRepository.save(new Group(parseDataKubsu.get(EParseKubsuData.GROUP_NAME), specialty));

        Group group = groupRepository
                .findByNameAndSpecialty(
                        parseDataKubsu.get(EParseKubsuData.GROUP_NAME), specialty)
                .orElseThrow(() ->
                        new GroupNotFoundException("unable to find group " + parseDataKubsu.get(EParseKubsuData.GROUP_NAME)));

        User user = new User();
        user.setKubsuUserId(Long.valueOf(parseDataKubsu.get(EParseKubsuData.KUBSU_USER_ID)));
        user.setUsername(username);
        user.setFullName(parseDataKubsu.get(EParseKubsuData.FULL_NAME));
        user.setEmail(parseDataKubsu.get(EParseKubsuData.EMAIL));
        user.setPassword(hashedPassword);
        user.setGroup(group);
        user.setStartEducationDate(null);
        user.setEndEducationDate(null);
        user.setCreationDate(OffsetDateTime.now());
        user.setRoles(roles);

        userRepository.save(user);
    }

    private Map<EParseKubsuData, String> authAndParseKubsuStudent(String user, String password) {
        Map<EParseKubsuData, String> studentData = new HashMap<>();
        try {
            Connection.Response response = Jsoup.connect("https://kubsu.ru/user/")
                    .data("name", user,
                            "pass", password,
                            "form_id", "user_login")
                    .method(Connection.Method.POST)
                    .timeout(10000).execute();
            log.debug(response.statusMessage());

            Document documentPortfolio = Jsoup.connect("https://www.kubsu.ru/public-portfolio")
                    .cookies(response.cookies()).get();
            log.debug("Parse portfolio: " + documentPortfolio.head());

            log.debug("Parse fullname...");
            String [] fio = documentPortfolio.select("head > title").text().split(Pattern.quote("|"));
            if (fio[0].trim().equals("Гость"))
                throw new IllegalArgumentException("invalid username or password");
            studentData.put(EParseKubsuData.FULL_NAME, fio[0].trim());

            log.debug("Parse group...");
            Element groupElement = documentPortfolio.selectFirst("head meta[about*=\"/ru/taxonomy/term/\"]");
            String group = Objects.requireNonNull(groupElement).attr("content");
            studentData.put(EParseKubsuData.GROUP_NAME, group);

            Element directionElement = documentPortfolio
                    .selectFirst("div.ds-1col.entity.entity-student-edu.student-edu-student-edu.view-mode-teaser.clearfix");
            Element subDirectionElement = Objects.requireNonNull(directionElement).selectFirst("h2");
            String facultyAndDirection = Objects.requireNonNull(subDirectionElement).text();

            String [] splitFacultyAndDirection = facultyAndDirection.split(Pattern.quote(" - "));

            log.debug("Parse faculty and specialty...");
            studentData.put(EParseKubsuData.FACULTY_NAME, splitFacultyAndDirection[0].trim());
            studentData.put(EParseKubsuData.SPECIALTY_NAME, splitFacultyAndDirection[1].trim());

            log.debug("Parse /user site...");
            Document documentUser = Jsoup.connect("https://www.kubsu.ru/user").cookies(response.cookies()).get();

            log.debug("Parse kubsu user id...");
            Element kubsuIdElement = documentUser.selectFirst("head meta[about*=\"/ru/user/\"]");
            String kubsuId = Objects.requireNonNull(kubsuIdElement).attr("resource");
            kubsuId = kubsuId.substring(kubsuId.lastIndexOf("/") + 1);
            studentData.put(EParseKubsuData.KUBSU_USER_ID, kubsuId);

            log.debug("Parse /user/" + kubsuId + "/edit...");
            Document documentUserEdit = Jsoup.connect("https://www.kubsu.ru/user/" + kubsuId + "/edit")
                    .cookies(response.cookies()).get();
            Element emailElement = documentUserEdit.selectFirst("input#edit-mail");
            studentData.put(EParseKubsuData.EMAIL, emailElement.attr("value"));

        } catch (IOException e) {
            log.error("Unexpected error during site parsing", e);
            e.printStackTrace();
        }

        return studentData;
    }

}
