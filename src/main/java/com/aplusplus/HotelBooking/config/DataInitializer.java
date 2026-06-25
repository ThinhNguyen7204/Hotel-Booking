package com.aplusplus.HotelBooking.config;

import com.aplusplus.HotelBooking.model.*;
import com.aplusplus.HotelBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private FacilityRepo facilityRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private PromotionRepo promotionRepo;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("--- Seeding Database with Realistic Mock Data (Safe Check-Before-Insert) ---");

        // 1. Create Users
        User admin = getOrCreateUser("admin@aurora.com", "System Administrator", "0123456789", "password", "ADMIN");
        User user = getOrCreateUser("user@aurora.com", "John Doe", "0987654321", "password", "USER");
        User emma = getOrCreateUser("emma@gmail.com", "Emma Watson", "0912345678", "password", "USER");
        User robert = getOrCreateUser("robert@gmail.com", "Robert Downey", "0911223344", "password", "USER");
        User sophie = getOrCreateUser("sophie@gmail.com", "Sophie Turner", "0933445566", "password", "USER");
        User michael = getOrCreateUser("michael@gmail.com", "Michael Jordan", "0988776655", "password", "USER");

        // 2. Create Facilities
        Facility f1 = createFacility(true, true, true, true, true, true, true, true);
        Facility f2 = createFacility(true, true, false, false, true, true, true, false);
        Facility f3 = createFacility(true, true, true, true, true, true, true, true);
        Facility f4 = createFacility(true, true, true, false, true, true, true, false);
        Facility f5 = createFacility(true, true, false, true, true, true, true, true);

        // 3. Create Rooms
        Room room1 = getOrCreateRoom("Deluxe Suite", "45", 150.0, "Spacious Deluxe Suite featuring a king-size bed, private balcony with mountain views, and a luxurious marble bathroom.", "Available", 2, 10, "https://images.unsplash.com/photo-1590490360182-c33d57733427?q=80&w=1000", f1);
        Room room2 = getOrCreateRoom("Superior Room", "35", 95.0, "Elegant Superior Room equipped with modern amenities, working desk, high-speed internet, and cozy queen-size bed.", "Available", 2, 15, "https://images.unsplash.com/photo-1566665797739-1674de7a421a?q=80&w=1000", f2);
        Room room3 = getOrCreateRoom("Presidential Penthouse", "120", 499.0, "The ultimate luxury penthouse experience. Includes 2 bedrooms, private jacuzzi, spacious living room, butler service, and panoramic ocean views.", "Available", 4, 2, "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=1000", f3);
        Room room4 = getOrCreateRoom("Family Executive", "65", 180.0, "Perfect for family vacations. Offers two connecting bedrooms, kid-friendly amenities, dining area, and beautiful garden vistas.", "Available", 4, 8, "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?q=80&w=1000", f4);
        Room room5 = getOrCreateRoom("Executive Club Room", "40", 130.0, "Designed for business travelers. Includes lounge access, complimentary laundry, ergonomic workspace, and premium coffee maker.", "Available", 2, 12, "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?q=80&w=1000", f5);

        // 4. Create Promotions
        Promotion promo1 = getOrCreatePromotion("Summer Getaway", "Enjoy summer at Aurora Grand Hotel with a 15% discount on all Deluxe Suites.", 15, LocalDate.now().minusDays(5), LocalDate.now().plusDays(30), "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1000", room1);
        Promotion promo2 = getOrCreatePromotion("Exclusive Suite Promo", "Experience our Presidential Penthouse with 20% discount.", 20, LocalDate.now().minusDays(2), LocalDate.now().plusDays(20), "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=1000", room3);

        // 5. Create Bookings & Payments
        Booking booking1 = getOrCreateBooking(LocalDate.now().minusDays(8), LocalDate.now().minusDays(5), 2, 0, 2, "AUR-DEL-001", 15, 382.5, room1, emma, true, 1000000001L);
        Booking booking2 = getOrCreateBooking(LocalDate.now().minusDays(6), LocalDate.now().minusDays(3), 2, 1, 3, "AUR-SUP-002", 0, 285.0, room2, robert, true, 1000000002L);
        Booking booking3 = getOrCreateBooking(LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), 2, 0, 2, "AUR-DEL-003", 15, 382.5, room1, user, true, 1000000003L);
        Booking booking4 = getOrCreateBooking(LocalDate.now().plusDays(5), LocalDate.now().plusDays(8), 1, 0, 1, "AUR-EXE-004", 0, 390.0, room5, user, false, 1000000004L);
        Booking booking5 = getOrCreateBooking(LocalDate.now().minusDays(2), LocalDate.now().plusDays(2), 4, 0, 4, "AUR-PRE-005", 20, 1596.8, room3, sophie, false, 1000000005L);
        Booking booking6 = getOrCreateBooking(LocalDate.now().minusDays(15), LocalDate.now().minusDays(13), 2, 0, 2, "AUR-SUP-006", 0, 190.0, room2, user, true, 1000000006L);

        // 6. Create Reviews
        getOrCreateReview(5, "Absolutely beautiful room! The balcony view was breathtaking and the service was top notch.", LocalDateTime.now().minusDays(6), 4, room1, emma);
        getOrCreateReview(5, "Loved the cozy beds and the high-speed internet. Deluxe Suite is worth it!", LocalDateTime.now().minusDays(1), 2, room1, user);
        getOrCreateReview(4, "Very clean and modern room. Great location, though the pool was a bit crowded.", LocalDateTime.now().minusDays(4), 1, room2, robert);
        getOrCreateReview(5, "Excellent value for money. Highly recommend the breakfast and the bath!", LocalDateTime.now().minusDays(13), 5, room2, user);
        getOrCreateReview(5, "Unparalleled luxury. The Presidential Penthouse is worth every single penny.", LocalDateTime.now().minusDays(10), 12, room3, michael);

        System.out.println("--- Seeding Check/Insertion Completed Successfully ---");
    }

    private User getOrCreateUser(String email, String name, String phoneNumber, String password, String role) {
        return userRepo.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            return userRepo.save(user);
        });
    }

    private Facility createFacility(boolean wifi, boolean breakfast, boolean pool, boolean gym, boolean parking, boolean bath, boolean coffee, boolean drink) {
        Facility f = new Facility();
        f.setWifiInfo(wifi);
        f.setBreakfastInfo(breakfast);
        f.setPoolInfo(pool);
        f.setGymInfo(gym);
        f.setParkingInfo(parking);
        f.setBathInfo(bath);
        f.setCoffeeInfo(coffee);
        f.setDrinkInfo(drink);
        return facilityRepo.save(f);
    }

    private Room getOrCreateRoom(String roomType, String roomSize, Double roomPrice, String roomDescription, String roomStatus, int capacity, int amount, String photoUrl, Facility facility) {
        List<Room> existing = roomRepo.findByRoomType(roomType);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomSize(roomSize);
        room.setRoomPrice(roomPrice);
        room.setRoomDescription(roomDescription);
        room.setRoomStatus(roomStatus);
        room.setRoomCapacity(capacity);
        room.setRoomAmount(amount);
        room.setRoomPhotoUrl(photoUrl);
        room.setFacility(facility);
        return roomRepo.save(room);
    }

    private Promotion getOrCreatePromotion(String title, String description, Integer discount, LocalDate start, LocalDate end, String photoUrl, Room room) {
        Promotion promo = promotionRepo.findAll().stream()
                .filter(p -> p.getPromotionTitle().equals(title))
                .findFirst()
                .orElse(null);
        if (promo != null) {
            return promo;
        }
        Promotion newPromo = new Promotion();
        newPromo.setPromotionTitle(title);
        newPromo.setDescription(description);
        newPromo.setPercentOfDiscount(discount);
        newPromo.setStartDate(start);
        newPromo.setEndDate(end);
        newPromo.setPromotionPhotoUrl(photoUrl);
        newPromo.setRooms(new ArrayList<>(List.of(room)));
        newPromo = promotionRepo.save(newPromo);

        room.getPromotions().add(newPromo);
        roomRepo.save(room);
        return newPromo;
    }

    private Booking getOrCreateBooking(LocalDate checkIn, LocalDate checkOut, int adults, int children, int totalGuests, String code, Integer discount, Double price, Room room, User user, boolean isPaid, Long paymentCode) {
        return bookingRepo.findByBookingCode(code).orElseGet(() -> {
            Booking booking = new Booking();
            booking.setCheckInDate(checkIn);
            booking.setCheckOutDate(checkOut);
            booking.setNumOfAdults(adults);
            booking.setNumOfChildren(children);
            booking.setTotalNumOfGuest(totalGuests);
            booking.setBookingCode(code);
            booking.setPercentOfDiscount(discount);
            booking.setFinalPrice(price);
            booking.setRoom(room);
            booking.setUser(user);
            booking = bookingRepo.save(booking);

            Payment payment = new Payment();
            payment.setPaymentCode(paymentCode);
            payment.setPaymentStatus(isPaid ? "PAID" : "UNPAID");
            payment.setUser(user);
            payment.setBooking(booking);
            paymentRepo.save(payment);
            return booking;
        });
    }

    private void getOrCreateReview(int rate, String comment, LocalDateTime created, int likes, Room room, User user) {
        boolean exists = reviewRepo.findAll().stream()
                .anyMatch(r -> r.getUser().getId().equals(user.getId()) && r.getRoom().getId().equals(room.getId()) && r.getComment().equals(comment));
        if (!exists) {
            Review review = new Review();
            review.setReviewRate(rate);
            review.setComment(comment);
            review.setCreatedTime(created);
            review.setLikeCounter(likes);
            review.setRoom(room);
            review.setUser(user);
            reviewRepo.save(review);
        }
    }
}
