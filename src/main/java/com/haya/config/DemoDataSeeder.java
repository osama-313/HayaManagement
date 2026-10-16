package com.haya.config;

import com.haya.entity.Commitment;
import com.haya.entity.Resource;
import com.haya.entity.Role;
import com.haya.entity.Task;
import com.haya.entity.User;
import com.haya.repository.CommitmentRepository;
import com.haya.repository.ResourceRepository;
import com.haya.repository.TaskRepository;
import com.haya.repository.UserRepository;
import com.haya.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Populates an empty database with a small, realistic team so the app is explorable straight away.
 * Enabled with {@code app.demo-data.enabled=true} (on by default in the {@code dev} profile).
 * Every demo account uses the password {@value #DEMO_PASSWORD}.
 */
@Component
@Order(2)
@ConditionalOnProperty(prefix = "app.demo-data", name = "enabled", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    static final String DEMO_PASSWORD = "Demo@2024";

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final RoleService roles;
    private final UserRepository users;
    private final TaskRepository tasks;
    private final CommitmentRepository commitments;
    private final ResourceRepository resources;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(RoleService roles, UserRepository users, TaskRepository tasks,
                          CommitmentRepository commitments, ResourceRepository resources,
                          PasswordEncoder passwordEncoder) {
        this.roles = roles;
        this.users = users;
        this.tasks = tasks;
        this.commitments = commitments;
        this.resources = resources;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() > 0) {
            return;
        }

        Role admin = roles.require(Role.ADMIN);
        Role member = roles.require(Role.USER);
        String password = passwordEncoder.encode(DEMO_PASSWORD);

        User alex = users.save(new User("Alex", "Morgan", "admin", "alex.morgan@haya.dev", password, 34, "Engineering Manager", admin));
        User sara = users.save(new User("Sara", "Lindqvist", "member", "sara.lindqvist@haya.dev", password, 29, "Frontend Engineer", member));
        User omar = users.save(new User("Omar", "Haddad", "omar", "omar.haddad@haya.dev", password, 31, "Backend Engineer", member));
        User priya = users.save(new User("Priya", "Nair", "priya", "priya.nair@haya.dev", password, 27, "Product Designer", member));
        User diego = users.save(new User("Diego", "Alvarez", "diego", "diego.alvarez@haya.dev", password, 36, "DevOps Engineer", member));

        LocalDate today = LocalDate.now();
        tasks.save(new Task("Ship the new onboarding flow", "Three-step wizard with progress saving and analytics events.", due(today, 6), false, sara));
        tasks.save(new Task("Migrate reports to the read replica", "Move heavy report queries off the primary database.", due(today, 12), false, omar));
        tasks.save(new Task("Design the settings redesign", "High-fidelity mockups for the account and team settings pages.", due(today, 3), false, priya));
        tasks.save(new Task("Set up blue/green deployments", "Zero-downtime releases with automatic rollback on failed health checks.", due(today, -2), false, diego));
        tasks.save(new Task("Fix flaky checkout test", "Intermittent timeout in the payment integration suite.", due(today, -5), true, omar));
        tasks.save(new Task("Quarterly roadmap review", "Prepare the slide deck and gather feedback from each team.", due(today, 9), false, alex));
        tasks.save(new Task("Accessibility audit of the dashboard", "Keyboard navigation, focus order and colour contrast.", due(today, 1), false, sara));
        tasks.save(new Task("Write the incident post-mortem", "Timeline, root cause and action items for last week's outage.", due(today, -1), true, diego));
        tasks.save(new Task("Upgrade to the latest LTS runtime", "Update CI images and run the full regression suite.", due(today, 15), false, omar));
        tasks.save(new Task("Create icon set v2", "Consistent 24px outline icons for the navigation.", due(today, -8), true, priya));

        commitments.save(new Commitment("09:00", "13:00", 4, "Deep work on the onboarding wizard.", sara));
        commitments.save(new Commitment("10:00", "12:00", 2, "Pairing session on query optimisation.", omar));
        commitments.save(new Commitment("13:00", "17:00", 4, "Usability testing with five customers.", priya));
        commitments.save(new Commitment("08:00", "12:00", 4, "Pipeline migration window.", diego));
        commitments.save(new Commitment("14:00", "16:00", 2, "One-to-ones with the team.", alex));

        resources.save(new Resource("angular.dev", "Official Angular documentation and tutorials."));
        resources.save(new Resource("https://spring.io/guides", "Hands-on Spring Boot guides, a good reference for new joiners."));
        resources.save(new Resource("https://web.dev/learn/accessibility", "Practical accessibility course used in our audits."));
        resources.save(new Resource("https://martinfowler.com/articles/microservices.html", "Background reading on service boundaries."));

        log.info("Seeded demo data; sign in as 'admin' or 'member' with password '{}'", DEMO_PASSWORD);
    }

    private static String due(LocalDate today, int offsetDays) {
        return today.plusDays(offsetDays).toString();
    }
}
