import java.util.Comparator;
import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("ry")
@Implements("UserComparator2")
public class UserComparator2 implements Comparator {
   @ObfuscatedName("f")
   @Export("reversed")
   final boolean reversed;

   public UserComparator2(boolean var1) {
      this.reversed = var1;
   }

   @ObfuscatedName("f")
   @ObfuscatedSignature(
      descriptor = "(Low;Low;I)I",
      garbageValue = "-1319150226"
   )
   @Export("compare_bridged")
   int compare_bridged(User var1, User var2) {
      return this.reversed ? var1.getUsername().compareToTyped(var2.getUsername()) : var2.getUsername().compareToTyped(var1.getUsername());
   }

   public boolean equals(Object var1) {
      return super.equals(var1);
   }

   public int compare(Object var1, Object var2) {
      return this.compare_bridged((User)var1, (User)var2);
   }
}
