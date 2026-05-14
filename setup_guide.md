# 🚀 KaushalyaKarnataka — Supabase Setup Guide

> Complete step-by-step instructions to set up your Supabase backend for the KaushalyaKarnataka Android app.

---

## Table of Contents

1. [Create a Supabase Project](#1-create-a-supabase-project)
2. [Get Your API Keys](#2-get-your-api-keys)
3. [Configure local.properties](#3-configure-localproperties)
4. [Enable Authentication](#4-enable-authentication)
5. [Create Database Tables](#5-create-database-tables)
6. [Create Database Functions & Triggers](#6-create-database-functions--triggers)
7. [Set Up Row Level Security (RLS) Policies](#7-set-up-row-level-security-rls-policies)
8. [Create Storage Buckets](#8-create-storage-buckets)
9. [Set Up Storage Policies](#9-set-up-storage-policies)
10. [Enable Realtime](#10-enable-realtime)
11. [Verification Checklist](#11-verification-checklist)
12. [Troubleshooting](#12-troubleshooting)

---

## 1. Create a Supabase Project

1. Go to [https://supabase.com](https://supabase.com) and sign in (or create an account).
2. Click **"New Project"**.
3. Fill in the details:
   - **Name**: `KaushalyaKarnataka`
   - **Database Password**: Choose a strong password (save it somewhere safe)
   - **Region**: Choose the closest region to your users (e.g., `South Asia (Mumbai)`)
4. Click **"Create new project"** and wait for provisioning (~2 minutes).

---

## 2. Get Your API Keys

1. In your Supabase dashboard, go to **Settings → API**.
2. Copy these two values:
   - **Project URL** — looks like `https://xxxxxxxxxxxx.supabase.co`
   - **anon (public) key** — a long JWT string starting with `eyJ...`

---

## 3. Configure local.properties

Open `local.properties` in your project root and add/update these lines:

```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
GEMINI_API_KEY=your-gemini-api-key-here
```

> ⚠️ **NEVER** commit `local.properties` to version control. It's already in `.gitignore` by default.

---

## 4. Enable Authentication

1. In Supabase dashboard, go to **Authentication → Providers**.
2. Ensure **Email** provider is **enabled** (it's enabled by default).
3. Configure email settings:
   - Go to **Authentication → Settings**
   - Under **Email Auth**:
     - ✅ Enable Email Signup
     - ✅ Enable Email Confirmations (optional — disable for development)
   - Under **General**:
     - Set **Site URL** to your app's deep link (e.g., `com.kaushalya.karnataka://callback`)
4. **(Optional)** To allow anonymous sign-in:
   - Go to **Authentication → Settings**
   - Scroll to **Anonymous Sign-ins**
   - Toggle **Enable anonymous sign-ins** to ON

---

## 5. Create Database Tables

Go to **SQL Editor** in your Supabase dashboard and run the following SQL scripts **one at a time**.

### 5.1 — Workers Table

```sql
CREATE TABLE public.workers (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name TEXT NOT NULL DEFAULT '',
    trade_category TEXT NOT NULL DEFAULT '',
    phone TEXT NOT NULL DEFAULT '',
    profile_photo_url TEXT DEFAULT '',
    bio TEXT DEFAULT '',
    average_rating DOUBLE PRECISION DEFAULT 0.0,
    total_ratings INTEGER DEFAULT 0,
    location TEXT DEFAULT '',
    latitude DOUBLE PRECISION DEFAULT 0.0,
    longitude DOUBLE PRECISION DEFAULT 0.0,
    experience_years INTEGER DEFAULT 0,
    is_available BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,
    role TEXT DEFAULT 'worker',
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Index for faster category filtering
CREATE INDEX idx_workers_trade_category ON public.workers(trade_category);

-- Index for sorting by rating
CREATE INDEX idx_workers_average_rating ON public.workers(average_rating DESC);

-- Index for search (name, location)
CREATE INDEX idx_workers_name ON public.workers USING gin(name gin_trgm_ops);
CREATE INDEX idx_workers_location ON public.workers USING gin(location gin_trgm_ops);
```

> **Note:** If the `gin_trgm_ops` indexes fail, enable the extension first:
> ```sql
> CREATE EXTENSION IF NOT EXISTS pg_trgm;
> ```
> Then re-run the index creation.

### 5.2 — Services Table

```sql
CREATE TABLE public.services (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    worker_id TEXT NOT NULL REFERENCES public.workers(id) ON DELETE CASCADE,
    service_name TEXT NOT NULL DEFAULT '',
    category TEXT NOT NULL DEFAULT '',
    price_type TEXT DEFAULT 'Fixed',
    price DOUBLE PRECISION DEFAULT 0.0,
    description TEXT DEFAULT '',
    estimated_time TEXT DEFAULT '',
    is_active BOOLEAN DEFAULT true
);

-- Index for worker lookup
CREATE INDEX idx_services_worker_id ON public.services(worker_id);
```

### 5.3 — Reviews Table

```sql
CREATE TABLE public.reviews (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    worker_id TEXT NOT NULL REFERENCES public.workers(id) ON DELETE CASCADE,
    customer_id TEXT NOT NULL DEFAULT '',
    customer_name TEXT NOT NULL DEFAULT '',
    rating REAL NOT NULL DEFAULT 0,
    review_text TEXT DEFAULT '',
    is_recommended BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Index for worker lookup
CREATE INDEX idx_reviews_worker_id ON public.reviews(worker_id);

-- Index for sorting by date
CREATE INDEX idx_reviews_created_at ON public.reviews(created_at DESC);
```

### 5.4 — Hire Requests Table

```sql
CREATE TABLE public.hire_requests (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    worker_id TEXT NOT NULL REFERENCES public.workers(id) ON DELETE CASCADE,
    customer_id TEXT NOT NULL DEFAULT '',
    customer_name TEXT NOT NULL DEFAULT '',
    customer_phone TEXT NOT NULL DEFAULT '',
    service_requested TEXT NOT NULL DEFAULT '',
    description TEXT DEFAULT '',
    preferred_timing TEXT DEFAULT '',
    status TEXT DEFAULT 'Pending',
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Index for worker and customer lookups
CREATE INDEX idx_hire_requests_worker_id ON public.hire_requests(worker_id);
CREATE INDEX idx_hire_requests_customer_id ON public.hire_requests(customer_id);

-- Index for sorting by date
CREATE INDEX idx_hire_requests_created_at ON public.hire_requests(created_at DESC);
```

### 5.5 — Notifications Table

```sql
CREATE TABLE public.notifications (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id TEXT NOT NULL DEFAULT '',
    title TEXT NOT NULL DEFAULT '',
    message TEXT NOT NULL DEFAULT '',
    type TEXT DEFAULT '',
    is_read BOOLEAN DEFAULT false,
    reference_id TEXT DEFAULT '',
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Index for user lookup
CREATE INDEX idx_notifications_user_id ON public.notifications(user_id);

-- Index for sorting by date
CREATE INDEX idx_notifications_created_at ON public.notifications(created_at DESC);
```

---

## 6. Create Database Functions & Triggers

These automate tasks like updating `updated_at` timestamps and recalculating worker ratings.

### 6.1 — Auto-update `updated_at` on Hire Requests

```sql
CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_hire_requests_updated_at
    BEFORE UPDATE ON public.hire_requests
    FOR EACH ROW
    EXECUTE FUNCTION public.update_updated_at_column();
```

### 6.2 — Auto-recalculate Worker Rating on New Review

```sql
CREATE OR REPLACE FUNCTION public.recalculate_worker_rating()
RETURNS TRIGGER
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    avg_val DOUBLE PRECISION;
    count_val INTEGER;
BEGIN
    SELECT AVG(rating), COUNT(*)
    INTO avg_val, count_val
    FROM public.reviews
    WHERE worker_id = NEW.worker_id;

    UPDATE public.workers
    SET average_rating = COALESCE(avg_val, 0),
        total_ratings = COALESCE(count_val, 0)
    WHERE id = NEW.worker_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Drop and recreate trigger to ensure it uses the updated function
DROP TRIGGER IF EXISTS trigger_recalculate_rating ON public.reviews;

CREATE TRIGGER trigger_recalculate_rating
    AFTER INSERT OR DELETE ON public.reviews
    FOR EACH ROW
    EXECUTE FUNCTION public.recalculate_worker_rating();
```

### 6.3 — Auto-create Notification on Hire Request

```sql
CREATE OR REPLACE FUNCTION public.notify_on_hire_request()
RETURNS TRIGGER AS $$
BEGIN
    -- Notify the worker when a new hire request is created
    INSERT INTO public.notifications (user_id, title, message, type, reference_id)
    VALUES (
        NEW.worker_id,
        'New Hire Request',
        NEW.customer_name || ' wants to hire you for ' || NEW.service_requested,
        'hire_request',
        NEW.id
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_notify_hire_request
    AFTER INSERT ON public.hire_requests
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_on_hire_request();
```

### 6.4 — Auto-create Notification on Status Change

```sql
CREATE OR REPLACE FUNCTION public.notify_on_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        -- Notify the customer about status change
        INSERT INTO public.notifications (user_id, title, message, type, reference_id)
        VALUES (
            NEW.customer_id,
            'Request ' || NEW.status,
            'Your hire request for ' || NEW.service_requested || ' has been ' || NEW.status,
            'status_update',
            NEW.id
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_notify_status_change
    AFTER UPDATE ON public.hire_requests
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_on_status_change();
```

---

## 7. Set Up Row Level Security (RLS) Policies

RLS ensures users can only access data they're authorized to see/modify.

### 7.1 — Enable RLS on All Tables

```sql
ALTER TABLE public.workers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.services ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.hire_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
```

### 7.2 — Workers Policies

```sql
-- Anyone can view workers
CREATE POLICY "Workers are publicly readable"
    ON public.workers FOR SELECT
    USING (true);

-- Authenticated users can create their own worker profile
CREATE POLICY "Users can create their own worker profile"
    ON public.workers FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid()::text = id);

-- Workers can update their own profile
CREATE POLICY "Workers can update their own profile"
    ON public.workers FOR UPDATE
    TO authenticated
    USING (auth.uid()::text = id)
    WITH CHECK (auth.uid()::text = id);

-- Workers can delete their own profile
CREATE POLICY "Workers can delete their own profile"
    ON public.workers FOR DELETE
    TO authenticated
    USING (auth.uid()::text = id);
```

### 7.3 — Services Policies

```sql
-- Anyone can view services
CREATE POLICY "Services are publicly readable"
    ON public.services FOR SELECT
    USING (true);

-- Workers can manage their own services
CREATE POLICY "Workers can create their own services"
    ON public.services FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid()::text = worker_id);

CREATE POLICY "Workers can update their own services"
    ON public.services FOR UPDATE
    TO authenticated
    USING (auth.uid()::text = worker_id)
    WITH CHECK (auth.uid()::text = worker_id);

CREATE POLICY "Workers can delete their own services"
    ON public.services FOR DELETE
    TO authenticated
    USING (auth.uid()::text = worker_id);
```

### 7.4 — Reviews Policies

```sql
-- Anyone can view reviews
CREATE POLICY "Reviews are publicly readable"
    ON public.reviews FOR SELECT
    USING (true);

-- Authenticated users can create reviews
CREATE POLICY "Authenticated users can create reviews"
    ON public.reviews FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid()::text = customer_id);
```

### 7.5 — Hire Requests Policies

```sql
-- Workers see their requests, customers see theirs
CREATE POLICY "Users can view their own hire requests"
    ON public.hire_requests FOR SELECT
    TO authenticated
    USING (
        auth.uid()::text = worker_id OR
        auth.uid()::text = customer_id
    );

-- Customers can create hire requests
CREATE POLICY "Customers can create hire requests"
    ON public.hire_requests FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid()::text = customer_id);

-- Workers can update request status, customers can cancel
CREATE POLICY "Involved users can update hire requests"
    ON public.hire_requests FOR UPDATE
    TO authenticated
    USING (
        auth.uid()::text = worker_id OR
        auth.uid()::text = customer_id
    );
```

### 7.6 — Notifications Policies

```sql
-- Users can view their own notifications
CREATE POLICY "Users can view their own notifications"
    ON public.notifications FOR SELECT
    TO authenticated
    USING (auth.uid()::text = user_id);

-- System (triggers) creates notifications, but allow insert for service_role
CREATE POLICY "System can create notifications"
    ON public.notifications FOR INSERT
    WITH CHECK (true);

-- Users can update their own notifications (mark as read)
CREATE POLICY "Users can update their own notifications"
    ON public.notifications FOR UPDATE
    TO authenticated
    USING (auth.uid()::text = user_id);
```

---

## 8. Create Storage Buckets

Go to **Storage** in your Supabase dashboard and create these buckets:

### 8.1 — Profiles Bucket

1. Click **"New Bucket"**
2. **Name**: `profiles`
3. **Public bucket**: ✅ Toggle **ON** (profile photos should be publicly accessible)
4. **File size limit**: `5MB`
5. **Allowed MIME types**: `image/jpeg, image/png, image/webp`
6. Click **"Create bucket"**

### 8.2 — Portfolios Bucket

1. Click **"New Bucket"**
2. **Name**: `portfolios`
3. **Public bucket**: ✅ Toggle **ON**
4. **File size limit**: `5MB`
5. **Allowed MIME types**: `image/jpeg, image/png, image/webp`
6. Click **"Create bucket"**

---

## 9. Set Up Storage Policies

Go to **Storage → Policies** or run these SQL statements in the SQL Editor:

### 9.1 — Profiles Bucket Policies

```sql
-- Anyone can view profile photos
CREATE POLICY "Public profile photo access"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'profiles');

-- Authenticated users can upload their own profile photo
CREATE POLICY "Users can upload their own profile photo"
    ON storage.objects FOR INSERT
    TO authenticated
    WITH CHECK (
        bucket_id = 'profiles' AND
        (storage.foldername(name))[1] = auth.uid()::text
    );

-- Authenticated users can update their own profile photo
CREATE POLICY "Users can update their own profile photo"
    ON storage.objects FOR UPDATE
    TO authenticated
    USING (
        bucket_id = 'profiles' AND
        (storage.foldername(name))[1] = auth.uid()::text
    );

-- Authenticated users can delete their own profile photo
CREATE POLICY "Users can delete their own profile photo"
    ON storage.objects FOR DELETE
    TO authenticated
    USING (
        bucket_id = 'profiles' AND
        (storage.foldername(name))[1] = auth.uid()::text
    );
```

### 9.2 — Portfolios Bucket Policies

```sql
-- Anyone can view portfolio photos
CREATE POLICY "Public portfolio access"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'portfolios');

-- Authenticated users can upload to their own folder
CREATE POLICY "Users can upload their own portfolio photos"
    ON storage.objects FOR INSERT
    TO authenticated
    WITH CHECK (
        bucket_id = 'portfolios' AND
        (storage.foldername(name))[1] = auth.uid()::text
    );

-- Authenticated users can update their own portfolio photos
CREATE POLICY "Users can update their own portfolio photos"
    ON storage.objects FOR UPDATE
    TO authenticated
    USING (
        bucket_id = 'portfolios' AND
        (storage.foldername(name))[1] = auth.uid()::text
    );

-- Authenticated users can delete their own portfolio photos
CREATE POLICY "Users can delete their own portfolio photos"
    ON storage.objects FOR DELETE
    TO authenticated
    USING (
        bucket_id = 'portfolios' AND
        (storage.foldername(name))[1] = auth.uid()::text
    );
```

---

## 10. Enable Realtime

The app uses the Supabase Realtime module. Enable it for the tables you want live updates on:

1. Go to **Database → Replication** in the dashboard.
2. Under **Realtime**, click **"Source"** next to `supabase_realtime`.
3. Toggle ON the following tables:
   - ✅ `workers`
   - ✅ `hire_requests`
   - ✅ `notifications`
4. Click **"Save"**.

Alternatively, run this SQL:

```sql
ALTER PUBLICATION supabase_realtime ADD TABLE public.workers;
ALTER PUBLICATION supabase_realtime ADD TABLE public.hire_requests;
ALTER PUBLICATION supabase_realtime ADD TABLE public.notifications;
```

---

## 11. Verification Checklist

After completing all the steps above, verify everything is working:

### ✅ Database Tables
- [ ] `workers` table exists with all columns
- [ ] `services` table exists with FK to `workers`
- [ ] `reviews` table exists with FK to `workers`
- [ ] `hire_requests` table exists with FK to `workers`
- [ ] `notifications` table exists

### ✅ RLS Policies
- [ ] RLS is enabled on **all 5 tables**
- [ ] Each table has SELECT, INSERT, UPDATE policies as described
- [ ] Test: unauthenticated user can read workers but **cannot** insert

### ✅ Storage
- [ ] `profiles` bucket exists and is **public**
- [ ] `portfolios` bucket exists and is **public**
- [ ] Storage policies are applied for both buckets

### ✅ Triggers & Functions
- [ ] `update_updated_at_column` function exists
- [ ] `recalculate_worker_rating` function exists
- [ ] `notify_on_hire_request` function exists
- [ ] `notify_on_status_change` function exists

### ✅ Authentication
- [ ] Email provider is enabled
- [ ] Anonymous sign-in is enabled (if needed)

### ✅ Realtime
- [ ] `workers`, `hire_requests`, `notifications` are added to realtime publication

### ✅ API Keys
- [ ] `SUPABASE_URL` is set in `local.properties`
- [ ] `SUPABASE_ANON_KEY` is set in `local.properties`
- [ ] `GEMINI_API_KEY` is set in `local.properties`

---

## 12. Troubleshooting

### "Permission denied" errors
- Make sure RLS is enabled AND policies are created. Enabling RLS without policies **blocks all access**.
- Check that the `auth.uid()::text` matches the `id` / `worker_id` / `customer_id` field in the relevant table.

### "Relation does not exist" errors
- Tables may not have been created. Go to **Table Editor** and verify all 5 tables exist.

### Storage upload fails
- Make sure the bucket is **public**.
- Check that storage policies allow INSERT for authenticated users.
- File must be under the size limit (5MB) and a supported MIME type.

### Anonymous users can't do anything
- Anonymous sign-in must be enabled in **Authentication → Settings**.
- Anonymous users are `authenticated` role in Supabase, so all `TO authenticated` policies apply.

### Realtime not working
- Verify the tables are added to the `supabase_realtime` publication.
- Ensure the Realtime module is installed in `AppModule.kt` (already done in this project).

### Build fails with empty SUPABASE_URL
- Ensure `local.properties` has no extra spaces around `=` signs.
- Sync Gradle after editing `local.properties`.

---

## Quick Reference: Table ↔ Code Mapping

| Supabase Table    | Kotlin Model    | Repository Method(s)                        |
|-------------------|-----------------|---------------------------------------------|
| `workers`         | `Worker`        | `getAllWorkers()`, `getWorkerById()`, etc.   |
| `services`        | `Service`       | `getServicesForWorker()`, `addService()`     |
| `reviews`         | `Review`        | `getReviewsForWorker()`, `addReview()`       |
| `hire_requests`   | `HireRequest`   | `sendHireRequest()`, `getHireRequestsFor*()` |
| `notifications`   | `Notification`  | `getNotifications()`, `markNotificationRead()` |

| Storage Bucket | Usage                   | Repository Method(s)           |
|----------------|-------------------------|--------------------------------|
| `profiles`     | Worker profile photos   | `uploadProfilePhoto()`         |
| `portfolios`   | Worker portfolio images | `uploadPortfolioPhoto()`, `getPortfolioPhotos()` |

---

> **You're all set!** 🎉 Build and run the app. The Supabase backend should now be fully functional.
