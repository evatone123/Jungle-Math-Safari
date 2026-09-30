package com.example.data.model

import com.example.data.local.entities.LevelProgressEntity

/**
 * Real animal entry in the Jungle Encyclopedia.
 * Unlocks as the child makes progress and reaches star milestones in various math zones.
 */
data class JungleAnimalEntry(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val emoji: String,
    val zone: MathTopic,
    val zoneName: String,
    val habitat: String,
    val diet: String,
    val sizeAndWeight: String,
    val topSpeedOrStat: String,
    val vocalSoundDescription: String,
    val funFacts: List<String>,
    val mathConnection: String,
    val requiredStarsInZone: Int = 0,
    val requiredZoneLevel: Int = 1,
    val requiredTotalStars: Int = 0,
    val isUnlocked: Boolean = false,
    val soundSpeechPhrase: String = ""
) {
    val milestoneRequirement: String
        get() = when {
            requiredZoneLevel > 1 -> "Complete Level $requiredZoneLevel in $zoneName"
            requiredStarsInZone > 0 -> "Earn $requiredStarsInZone ⭐ in $zoneName"
            requiredTotalStars > 0 -> "Earn $requiredTotalStars ⭐ across the Safari"
            else -> "Unlocked by exploring $zoneName"
        }
}

object JungleEncyclopediaData {
    val ALL_ANIMALS: List<JungleAnimalEntry> = listOf(
        // Counting Creek Animals
        JungleAnimalEntry(
            id = "counting_creek_capybara",
            commonName = "Giant Capybara",
            scientificName = "Hydrochoerus hydrochaeris",
            emoji = "🦫",
            zone = MathTopic.COUNTING,
            zoneName = "Counting Creek",
            habitat = "Riverbanks, dense swamp edges, and tropical creeks",
            diet = "Grasses, aquatic reeds, fruits, and tree bark",
            sizeAndWeight = "Up to 1.3 meters long, weighing 35 to 65 kg",
            topSpeedOrStat = "Can hold breath underwater for up to 5 minutes!",
            vocalSoundDescription = "Gentle clicks, whistles, and low friendly barks",
            funFacts = listOf(
                "Capybaras are the largest living rodents in the entire world!",
                "They have webbed feet that make them champion swimmers in jungle rivers.",
                "Birds, monkeys, and even turtles love sitting on capybaras because they are so calm and peaceful."
            ),
            mathConnection = "Count by 5s! Capybaras often hang out in friendly family groups of 10 to 20 capybaras.",
            requiredStarsInZone = 0,
            requiredZoneLevel = 1,
            soundSpeechPhrase = "Capybaras are the biggest friendly rodents in the world and love swimming in Counting Creek!"
        ),
        JungleAnimalEntry(
            id = "counting_creek_otter",
            commonName = "Giant River Otter",
            scientificName = "Pteronura brasiliensis",
            emoji = "🦦",
            zone = MathTopic.COUNTING,
            zoneName = "Counting Creek",
            habitat = "Freshwater slow-moving jungle rivers and flooded creeks",
            diet = "Catfish, characins, crabs, and small caimans",
            sizeAndWeight = "Up to 1.8 meters long, weighing up to 32 kg",
            topSpeedOrStat = "Can swim up to 14 km/h with powerful rudder tails",
            vocalSoundDescription = "Loud river whistles, chuckles, and chatter",
            funFacts = listOf(
                "Known as the 'River Wolf' because family packs hunt together in rivers.",
                "Each river otter has unique creamy white throat markings like a human fingerprint!",
                "They can eat up to 4 kilograms of fresh river fish every single day."
            ),
            mathConnection = "Counting Fish: If an otter eats 4 fish in the morning and 4 in the afternoon, count the total: 1, 2, 3, 4... 8!",
            requiredStarsInZone = 4,
            requiredZoneLevel = 2,
            soundSpeechPhrase = "Giant River Otters hunt in family packs and communicate with nine different river whistles!"
        ),
        JungleAnimalEntry(
            id = "counting_creek_toucan",
            commonName = "Toco Toucan",
            scientificName = "Ramphastos toco",
            emoji = "🦜",
            zone = MathTopic.COUNTING,
            zoneName = "Counting Creek",
            habitat = "Semi-open forest canopy along river margins",
            diet = "Papayas, berries, figs, insects, and tree frogs",
            sizeAndWeight = "60 cm long with a magnificent 20 cm bill",
            topSpeedOrStat = "Beak is super light—mostly hollow air pockets!",
            vocalSoundDescription = "Deep frog-like croaks and loud clattering bill sounds",
            funFacts = listOf(
                "A toucan's giant bill looks heavy, but it is made of keratin honeycombs and weighs almost nothing!",
                "Toucans use their colorful bills like a thermal radiator to cool off in the humid jungle heat.",
                "They tuck their big bill under their wing and flip their tail up like a feather ball to sleep."
            ),
            mathConnection = "Count the Berries: A toucan tosses berries up in the air and catches them one by one. Practice counting up to 10!",
            requiredStarsInZone = 8,
            requiredZoneLevel = 3,
            soundSpeechPhrase = "Toco Toucans have huge rainbow bills that are super lightweight honeycombs of air!"
        ),
        JungleAnimalEntry(
            id = "counting_creek_frog",
            commonName = "Red-Eyed Tree Frog",
            scientificName = "Agalychnis callidryas",
            emoji = "🐸",
            zone = MathTopic.COUNTING,
            zoneName = "Counting Creek",
            habitat = "Rainforest canopies near freshwater ponds and streams",
            diet = "Crickets, moths, flies, and grasshoppers",
            sizeAndWeight = "5 to 7 cm, weighs less than a single pencil (15 grams)",
            topSpeedOrStat = "Can leap over 20 times its body length in one bound!",
            vocalSoundDescription = "Chack-chack mating calls during tropical night rain",
            funFacts = listOf(
                "When startled, they flash their bright red eyes to freeze predators for a split second!",
                "They have suction-cup toe pads that let them stick to wet waxy rainforest leaves effortlessly.",
                "Mother frogs lay eggs underneath leaves dangling right over water so tadpoles plop safely in when they hatch."
            ),
            mathConnection = "Leap Counting: Count by 2s or 3s as the frog hops from lily pad 2 to 4 to 6 to 8!",
            requiredStarsInZone = 12,
            requiredZoneLevel = 5,
            soundSpeechPhrase = "Red-eyed tree frogs flash their bright eyes to surprise predators while hopping between giant jungle leaves!"
        ),

        // Addition Woods Animals
        JungleAnimalEntry(
            id = "addition_woods_jaguar",
            commonName = "Spotted Jaguar",
            scientificName = "Panthera onca",
            emoji = "🐆",
            zone = MathTopic.ADDITION,
            zoneName = "Addition Woods",
            habitat = "Dense tropical rainforests and swamp woodlands",
            diet = "Deer, peccaries, tapirs, caimans, and capybaras",
            sizeAndWeight = "Up to 2 meters long, weighing 60 to 120 kg",
            topSpeedOrStat = "Strongest bite force of any big cat on Earth!",
            vocalSoundDescription = "Deep chest roars known as a 'sawing' rasp",
            funFacts = listOf(
                "Unlike most pet cats, jaguars absolutely love swimming and dive right into deep jungle rivers!",
                "Their rosette patterns are unique: each rosette has little dark spots inside the flower shape.",
                "The name jaguar comes from 'yaguar', meaning 'he who kills with one leap'."
            ),
            mathConnection = "Spot Addition: If a jaguar has 6 rosette spots on its left shoulder and 6 on its right, 6 + 6 = 12 spots!",
            requiredStarsInZone = 0,
            requiredZoneLevel = 1,
            soundSpeechPhrase = "Jaguars have the strongest bite of any big cat and love swimming through jungle rivers!"
        ),
        JungleAnimalEntry(
            id = "addition_woods_tapir",
            commonName = "South American Tapir",
            scientificName = "Tapirus terrestris",
            emoji = "🦏",
            zone = MathTopic.ADDITION,
            zoneName = "Addition Woods",
            habitat = "Lowland rainforest floors and dense undergrowth",
            diet = "Leaves, juicy twigs, jungle fruits, and water plants",
            sizeAndWeight = "Up to 2 meters long, weighing 150 to 250 kg",
            topSpeedOrStat = "Flexible prehensile snout acts like an elephant's trunk!",
            vocalSoundDescription = "High-pitched whistles and snorts to warn babies",
            funFacts = listOf(
                "Baby tapirs are born with camouflage white stripes and spots like little jungle watermelons!",
                "Tapirs are nicknamed the 'Gardeners of the Rainforest' because they disperse seeds over miles.",
                "They use their flexible snout like a snorkel when hiding underwater."
            ),
            mathConnection = "Seed Addition: A tapir eats 15 fruit seeds in the forest and 10 seeds near the river. 15 + 10 = 25 seeds planted!",
            requiredStarsInZone = 4,
            requiredZoneLevel = 2,
            soundSpeechPhrase = "Tapirs use their prehensile snoots like mini trunks to grab juicy leaves and plant future trees!"
        ),
        JungleAnimalEntry(
            id = "addition_woods_sloth",
            commonName = "Three-Toed Brown Sloth",
            scientificName = "Bradypus tridactylus",
            emoji = "🦥",
            zone = MathTopic.ADDITION,
            zoneName = "Addition Woods",
            habitat = "High forest canopy hanging upside-down in cecropia trees",
            diet = "Tender cecropia tree leaves and flower buds",
            sizeAndWeight = "50 to 60 cm, weighing around 4 kg",
            topSpeedOrStat = "Moves 0.24 km/h on land, but swims 3 times faster in water!",
            vocalSoundDescription = "Soft high-pitched 'ay-ay' squeaks",
            funFacts = listOf(
                "Sloths move so slowly that real green algae grows right on their fur, giving them natural camouflage!",
                "They sleep between 15 and 18 hours each day hanging peacefully by curved claws.",
                "Their extra neck vertebrae allow them to turn their head 270 degrees without moving their shoulders."
            ),
            mathConnection = "Sleeping Hours: 9 hours of day sleep + 7 hours of night sleep = 16 hours of cozy treetop resting!",
            requiredStarsInZone = 8,
            requiredZoneLevel = 3,
            soundSpeechPhrase = "Three-toed sloths move so slowly that harmless green algae grows right on their fur to hide them from harpy eagles!"
        ),
        JungleAnimalEntry(
            id = "addition_woods_harpy",
            commonName = "Harpy Eagle",
            scientificName = "Harpia harpyja",
            emoji = "🦅",
            zone = MathTopic.ADDITION,
            zoneName = "Addition Woods",
            habitat = "Emergent canopy layer of pristine lowland rainforest",
            diet = "Tree sloths, monkeys, and coatis",
            sizeAndWeight = "Wingspan over 2 meters; talons longer than grizzly bear claws (13 cm)!",
            topSpeedOrStat = "Can dive through dense tree branches at 80 km/h!",
            vocalSoundDescription = "Piercing territorial whistles across the canopy",
            funFacts = listOf(
                "The harpy eagle's rear talons are 13 cm long—larger than grizzly bear claws!",
                "They have double facial feather discs that funnel sound directly to their ears like satellite dishes.",
                "Pairs raise only one chick every two to three years, guarding the giant stick nest vigilantly."
            ),
            mathConnection = "Wingspan Addition: Left wing 100 cm + right wing 100 cm = 200 cm total wingspan glide!",
            requiredStarsInZone = 12,
            requiredZoneLevel = 5,
            soundSpeechPhrase = "Harpy eagles have talons as big as grizzly bear claws and navigate dense tree branches at high speeds!"
        ),

        // Subtraction Savannah Animals
        JungleAnimalEntry(
            id = "subtraction_savannah_lion",
            commonName = "African Lion",
            scientificName = "Panthera leo",
            emoji = "🦁",
            zone = MathTopic.SUBTRACTION,
            zoneName = "Subtraction Savannah",
            habitat = "Grassland plains, savannahs, and open woodlands",
            diet = "Zebras, wildebeest, buffalo, and antelopes",
            sizeAndWeight = "Up to 2.5 meters long, weighing 190 kg",
            topSpeedOrStat = "Can sprint up to 80 km/h in short bursts!",
            vocalSoundDescription = "A mighty roar that can be heard 8 kilometers away!",
            funFacts = listOf(
                "A lion's roar can be heard by humans and animals up to 8 kilometers (5 miles) away!",
                "Lions are the only big cats that live together in social family groups called prides.",
                "Female lions (lionesses) do over 85% of the hunting teamwork for the pride."
            ),
            mathConnection = "Roar Distance: If a roar travels 8 km and you are 3 km away, how much farther can it travel? 8 - 3 = 5 km!",
            requiredStarsInZone = 0,
            requiredZoneLevel = 1,
            soundSpeechPhrase = "African lions live in family prides and their thunderous roars carry over five miles across the plains!"
        ),
        JungleAnimalEntry(
            id = "subtraction_savannah_zebra",
            commonName = "Plains Zebra",
            scientificName = "Equus quagga",
            emoji = "🦓",
            zone = MathTopic.SUBTRACTION,
            zoneName = "Subtraction Savannah",
            habitat = "Treeless grasslands and savannah woodlands",
            diet = "Tall coarse grasses and shrubs",
            sizeAndWeight = "1.4 meters tall at shoulder, weighing 300 kg",
            topSpeedOrStat = "Can run up to 65 km/h zig-zagging away from predators",
            vocalSoundDescription = "High bark-whinny 'kwa-ha-ha' calls",
            funFacts = listOf(
                "Every single zebra's black and white stripe pattern is totally unique, like a barcode!",
                "Zebras sleep standing up by locking their knees so they can escape fast if needed.",
                "Their dazzling stripes confuse biting tsetse flies and create an optical illusion for predators."
            ),
            mathConnection = "Herd Subtraction: A herd of 20 zebras crosses the river. 7 stop to drink water. 20 - 7 = 13 zebras keep running!",
            requiredStarsInZone = 4,
            requiredZoneLevel = 2,
            soundSpeechPhrase = "Plains zebras have unique barcode stripe patterns that dazzle predators and confuse biting flies!"
        ),
        JungleAnimalEntry(
            id = "subtraction_savannah_giraffe",
            commonName = "Savannah Giraffe",
            scientificName = "Giraffa camelopardalis",
            emoji = "🦒",
            zone = MathTopic.SUBTRACTION,
            zoneName = "Subtraction Savannah",
            habitat = "Acacia savannahs and open thorny woodlands",
            diet = "Acacia leaves, thorny twigs, and apricot blooms",
            sizeAndWeight = "Up to 5.8 meters tall (tallest land mammal on Earth!)",
            topSpeedOrStat = "Has a 45-centimeter bluish-purple tongue that wraps around thorns!",
            vocalSoundDescription = "Low frequency nocturnal hums and gentle snorts",
            funFacts = listOf(
                "Giraffes are the tallest mammals on Earth, standing up to nearly 6 meters high!",
                "Even with their long necks, giraffes have 7 neck vertebrae—the exact same number as humans!",
                "Their bluish-black tongue is prehensile and has extra melanin so it does not get sunburned while eating all day."
            ),
            mathConnection = "Height Subtraction: A 6-meter adult giraffe stands next to a 2-meter baby giraffe. The difference is 6 - 2 = 4 meters!",
            requiredStarsInZone = 8,
            requiredZoneLevel = 3,
            soundSpeechPhrase = "Giraffes are the tallest mammals in the world and have 45-centimeter purple sunproof tongues!"
        ),
        JungleAnimalEntry(
            id = "subtraction_savannah_elephant",
            commonName = "African Bush Elephant",
            scientificName = "Loxodonta africana",
            emoji = "🐘",
            zone = MathTopic.SUBTRACTION,
            zoneName = "Subtraction Savannah",
            habitat = "Savannahs, marshes, river valleys, and mopane woodlands",
            diet = "Grass, tree bark, roots, bananas, and acacia pods",
            sizeAndWeight = "Up to 3.3 meters at the shoulder, weighing up to 6,000 kg",
            topSpeedOrStat = "Trunk contains over 40,000 individual muscles!",
            vocalSoundDescription = "Rumbling infrasound calls traveling through the ground",
            funFacts = listOf(
                "Elephants are the largest living land animals on planet Earth!",
                "An elephant's trunk is so precise it can pick up a single peanut or rip down an entire tree.",
                "They communicate with subterranean vibrations that other elephant herds feel through the soles of their feet."
            ),
            mathConnection = "Water Bath Subtraction: An elephant sucks up 10 liters of water into its trunk and sprays 6 liters onto its back. 10 - 6 = 4 liters left!",
            requiredStarsInZone = 12,
            requiredZoneLevel = 5,
            soundSpeechPhrase = "African elephants are the largest land animals and have over 40,000 muscles in their amazing trunks!"
        ),

        // Multiplication Canopy Animals
        JungleAnimalEntry(
            id = "multiplication_canopy_monkey",
            commonName = "Spider Monkey",
            scientificName = "Ateles geoffroyi",
            emoji = "🐒",
            zone = MathTopic.MULTIPLICATION,
            zoneName = "Multiplication Canopy",
            habitat = "Upper rainforest canopy of evergreen and semi-deciduous jungles",
            diet = "Figs, wild plums, nectar, and tender young leaves",
            sizeAndWeight = "Body 50 cm long, with an 85 cm tail weighing 8 kg",
            topSpeedOrStat = "Prehensile tail can support the monkey's entire body weight!",
            vocalSoundDescription = "High whistling whinnies and loud barking calls",
            funFacts = listOf(
                "Spider monkeys have an incredible fifth hand: their tail has a hairless grip pad on the tip!",
                "They do not have thumbs on their hands—hook-like four fingers help them swing through trees like trapeze artists.",
                "They can bridge huge gaps between jungle trees by holding each other's hands and tails!"
            ),
            mathConnection = "Banana Bunches: If a monkey finds 4 bunches of bananas with 5 bananas in each bunch, 4 × 5 = 20 delicious bananas!",
            requiredStarsInZone = 0,
            requiredZoneLevel = 1,
            soundSpeechPhrase = "Spider monkeys swing through the canopy using their strong tails like a fifth hand with a fingerprint grip!"
        ),
        JungleAnimalEntry(
            id = "multiplication_canopy_macaw",
            commonName = "Scarlet Macaw",
            scientificName = "Ara macao",
            emoji = "🦜",
            zone = MathTopic.MULTIPLICATION,
            zoneName = "Multiplication Canopy",
            habitat = "Emergent tall jungle trees and canopy cliffs",
            diet = "Hard jungle nuts, palm seeds, and clay from river licks",
            sizeAndWeight = "Up to 90 cm long, weighing about 1 kg",
            topSpeedOrStat = "Beak pressure can crack Brazil nuts that humans need a hammer for!",
            vocalSoundDescription = "Loud raucous 'raa-aak' calls carrying across valleys",
            funFacts = listOf(
                "Scarlet macaws mate for life and fly side-by-side with their wingtips almost touching!",
                "They eat clay from riverbanks because minerals in the clay neutralize toxins in unripe forest seeds.",
                "They can live up to 50 to 75 years in the tropical wild."
            ),
            mathConnection = "Feather Multiplication: A flock of 3 pairs of macaws flies overhead. Each pair is 2 birds. 3 × 2 = 6 colorful macaws!",
            requiredStarsInZone = 4,
            requiredZoneLevel = 2,
            soundSpeechPhrase = "Scarlet macaws mate for life, fly in pairs, and eat river clay to digest jungle seed minerals!"
        ),
        JungleAnimalEntry(
            id = "multiplication_canopy_chameleon",
            commonName = "Panther Chameleon",
            scientificName = "Furcifer pardalis",
            emoji = "🦎",
            zone = MathTopic.MULTIPLICATION,
            zoneName = "Multiplication Canopy",
            habitat = "Warm humid tropical foliage and forest edges",
            diet = "Crickets, mantises, grasshoppers, and small geckos",
            sizeAndWeight = "40 to 50 cm long, weighing 150 grams",
            topSpeedOrStat = "Tongue accelerates from 0 to 97 km/h in a fraction of a second!",
            vocalSoundDescription = "Gentle defense hisses and jaw displays",
            funFacts = listOf(
                "Chameleons change colors not just for camouflage, but to communicate emotions and temperature!",
                "Their eyes can swivel independently in two completely different directions at the exact same time.",
                "A chameleon's tongue is up to twice the length of its body and launches faster than a supersonic jet engine!"
            ),
            mathConnection = "Insect Catching: If a chameleon catches 3 insects every hour for 4 hours, 3 × 4 = 12 crunchy bugs!",
            requiredStarsInZone = 8,
            requiredZoneLevel = 3,
            soundSpeechPhrase = "Chameleons can move their eyes in two different directions simultaneously and shoot their tongues in milliseconds!"
        ),
        JungleAnimalEntry(
            id = "multiplication_canopy_gorilla",
            commonName = "Mountain Gorilla",
            scientificName = "Gorilla beringei beringei",
            emoji = "🦍",
            zone = MathTopic.MULTIPLICATION,
            zoneName = "Multiplication Canopy",
            habitat = "Cloud forests and bamboo slopes of high tropical volcanoes",
            diet = "Celery stalks, bamboo shoots, thistles, and wild ginger",
            sizeAndWeight = "Up to 1.7 meters tall, weighing 160 to 220 kg",
            topSpeedOrStat = "Up to 6 times stronger than an adult human champion!",
            vocalSoundDescription = "Deep resonant chest beating and friendly throat rumbles",
            funFacts = listOf(
                "Gorillas make a new soft bed of branches and leaves every single night—they never sleep in the same nest twice!",
                "Each gorilla's nose print is completely unique, like human fingerprints.",
                "They share approximately 98% of their genetic DNA with humans and communicate with over 25 distinct vocal sounds."
            ),
            mathConnection = "Bamboo Bundles: A silverback gorilla family gathers 5 piles of bamboo shoots with 6 shoots in each pile. 5 × 6 = 30 crunchy snacks!",
            requiredStarsInZone = 12,
            requiredZoneLevel = 5,
            soundSpeechPhrase = "Mountain gorillas build fresh leaf beds every night and have nose prints as unique as human fingerprints!"
        )
    )

    /**
     * Compute unlock statuses based on actual progress in the database.
     */
    fun getPopulatedEncyclopedia(
        levels: List<LevelProgressEntity>,
        totalStars: Int
    ): List<JungleAnimalEntry> {
        val starsByTopic = mutableMapOf<String, Int>()
        val maxLevelByTopic = mutableMapOf<String, Int>()

        levels.forEach { level ->
            starsByTopic[level.areaTopic] = (starsByTopic[level.areaTopic] ?: 0) + level.starsEarned
            if (level.starsEarned > 0) {
                val currentMax = maxLevelByTopic[level.areaTopic] ?: 0
                if (level.levelNumber > currentMax) {
                    maxLevelByTopic[level.areaTopic] = level.levelNumber
                }
            }
        }

        return ALL_ANIMALS.map { animal ->
            val zoneStars = starsByTopic[animal.zone.id] ?: 0
            val maxLevelCompleted = maxLevelByTopic[animal.zone.id] ?: 0

            // Unlocked if child has cleared the level or collected sufficient stars
            val isUnlocked = (animal.requiredZoneLevel == 1 && animal.requiredStarsInZone == 0) ||
                    (zoneStars >= animal.requiredStarsInZone && animal.requiredStarsInZone > 0) ||
                    (maxLevelCompleted >= animal.requiredZoneLevel) ||
                    (totalStars >= animal.requiredTotalStars && animal.requiredTotalStars > 0)

            animal.copy(isUnlocked = isUnlocked)
        }
    }
}
